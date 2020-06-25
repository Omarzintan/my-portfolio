// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.Collections;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public final class FindMeetingQuery {
  private static final TimeRange wholeDay = TimeRange.WHOLE_DAY;
  private static final int startDay = TimeRange.START_OF_DAY;
  private static final int endDay = TimeRange.END_OF_DAY;
  private static final String OVERLAP_CONDITION = "overlaps";
  private static final String NESTED_CONDITION = "nested";
  private static final String NO_SPECIAL_CONDITION = "allGood";
  private static final Collection<String> optionalAttendees = new ArrayList<String>();

  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Collection<String> attendees = new ArrayList<String>();
    attendees.addAll(request.getAttendees());
    optionalAttendees.addAll(request.getOptionalAttendees());
    long meetingDuration = request.getDuration();
    Collection<TimeRange> setOfTimeRanges = new ArrayList<TimeRange>();
    int numberOfOptionalAttendeesAdded = 0;

    if (attendees.isEmpty() && optionalAttendees.isEmpty()) {
      setOfTimeRanges.add(wholeDay);
      return setOfTimeRanges;
    }

    // Take care of requests too long.
    long requestDuration = request.getDuration();
    if (requestDuration > wholeDay.duration()) { 
      return setOfTimeRanges;
    }
    
    // Checking for nested events.
    if (conditionExists(events, attendees, NESTED_CONDITION)) {
      return dealWithCondition(events, attendees, NESTED_CONDITION, meetingDuration, numberOfOptionalAttendeesAdded);
    }
    // Checking for overlapping events among mandatory attendees.
    if (conditionExists(events, attendees, OVERLAP_CONDITION)){
      return dealWithCondition(events, attendees, OVERLAP_CONDITION, meetingDuration, numberOfOptionalAttendeesAdded);
    }

    // Checking for overlapping events with optional attendees.
    if (attendees.isEmpty()) {
      boolean optionalAttendeesOverlap = false;
      attendees.addAll(optionalAttendees);
      numberOfOptionalAttendeesAdded = attendees.size();
      if (conditionExists(events, attendees, NESTED_CONDITION)) {
        return dealWithCondition(events, attendees, NESTED_CONDITION, meetingDuration, numberOfOptionalAttendeesAdded);
      }
      if ((conditionExists(events, attendees, OVERLAP_CONDITION))) {
        return dealWithCondition(events, attendees, OVERLAP_CONDITION, meetingDuration, numberOfOptionalAttendeesAdded);
      }
    }
    
    for (String optionalAttendee : optionalAttendees) {
      Collection<String> updatedAttendeesList = new ArrayList<String>();
      updatedAttendeesList.addAll(attendees);
      updatedAttendeesList.add(optionalAttendee);
      if (!(conditionExists(events, updatedAttendeesList, OVERLAP_CONDITION))) {
        attendees.add(optionalAttendee);
        numberOfOptionalAttendeesAdded++;
      }
    }
    // No conditions.
    return dealWithCondition(events, attendees, NO_SPECIAL_CONDITION, meetingDuration, numberOfOptionalAttendeesAdded);
  }

/** Splits day into two "free-to-meet" options before and after an event */
  private List<TimeRange> eventSplit(TimeRange timeRange) {
    List<TimeRange> eventTimeRanges = new ArrayList<TimeRange>();
    TimeRange beforeEvent = TimeRange.fromStartEnd(startDay, timeRange.start(), false);
    TimeRange afterEvent = TimeRange.fromStartEnd(timeRange.end(), endDay, true);
    eventTimeRanges.add(beforeEvent);
    eventTimeRanges.add(afterEvent); 
    return eventTimeRanges;
  }

  /** Creates list of possible free times for users with different events given an ordered list of time ranges */
  private Collection<TimeRange> considerEveryAttendee(List<TimeRange> orderedListOfTimeRanges, long meetingDuration, int numberOfOptionalAttendeesAdded, List<TimeRange>unorderedListOfTimeRanges) {
    Collection<TimeRange> collectionOfFreeTimes = new ArrayList<TimeRange>();

    // Take care of corner case where there is only one event.
    if (orderedListOfTimeRanges.size() == 1) {
      TimeRange onlyEvent = orderedListOfTimeRanges.get(0);
      collectionOfFreeTimes.add(eventSplit(onlyEvent).get(0));
      collectionOfFreeTimes.add(eventSplit(onlyEvent).get(1));
      return collectionOfFreeTimes;
    }

    // Take the earliest event.
    TimeRange firstEvent = orderedListOfTimeRanges.get(0);
    
    // Split free time into before and after the event.
    TimeRange beforeFirstEvent = eventSplit(firstEvent).get(0);
    TimeRange afterFirstEvent = eventSplit(firstEvent).get(1);
    
    // Let first free time be before the first event.
    if (enoughRoom(beforeFirstEvent, meetingDuration)) {
        collectionOfFreeTimes.add(beforeFirstEvent);
    }
    else if (numberOfOptionalAttendeesAdded > 0) {
      while (numberOfOptionalAttendeesAdded != 0) {
        unorderedListOfTimeRanges.remove(unorderedListOfTimeRanges.size()-1);
        numberOfOptionalAttendeesAdded--;
      } 
      Collections.sort(unorderedListOfTimeRanges, TimeRange.ORDER_BY_END);
      orderedListOfTimeRanges = unorderedListOfTimeRanges;
    }

    // Loop starting from next event.
    for (int i = 1; i < orderedListOfTimeRanges.size(); i++) {
      TimeRange currentEvent = orderedListOfTimeRanges.get(i);
      if (afterFirstEvent.overlaps(currentEvent)) {
        TimeRange nextFreeSlot = TimeRange.fromStartEnd(afterFirstEvent.start(), currentEvent.start(), false);
        afterFirstEvent = TimeRange.fromStartEnd(currentEvent.end(), afterFirstEvent.end(), false);
        if (enoughRoom(nextFreeSlot, meetingDuration)) {
          collectionOfFreeTimes.add(nextFreeSlot);
        }
      }
    }
    
    // For remaining freeSlot that has no clashes.
    TimeRange nextFreeSlot = afterFirstEvent;
    if (enoughRoom(nextFreeSlot, meetingDuration)) {
          collectionOfFreeTimes.add(nextFreeSlot);
    }
    return collectionOfFreeTimes;
  } 

  /** Checks if a condition exists given a string describing the condition */
  private boolean conditionExists(Collection<Event> events, Collection<String> attendees, String condition) {
    List<TimeRange> relevantTimeRanges = new ArrayList<TimeRange>();
    for (Event e : events) {
      for (String attendee : attendees) {
        if (e.getAttendees().contains(attendee)) {
          relevantTimeRanges.add(e.getWhen());
        }
      }
    }

    Collections.sort(relevantTimeRanges, TimeRange.ORDER_BY_START);
    boolean isCondition = false;

    if (condition == OVERLAP_CONDITION) {
      for (int i = 0; i < relevantTimeRanges.size(); i++) {
        TimeRange currentTimeRange = relevantTimeRanges.get(i);
        TimeRange nextTimeRange = (i+1) < relevantTimeRanges.size() ? relevantTimeRanges.get(i+1) : null;
        if (nextTimeRange != null && currentTimeRange.overlaps(nextTimeRange)) {
          isCondition = true;
          System.out.println("current event: " + currentTimeRange +" Next event: "+ nextTimeRange);
          return isCondition;
        }
      }
      return isCondition;
    }

    else if (condition == NESTED_CONDITION) {
      for (int i = 0; i < relevantTimeRanges.size(); i++) {
        TimeRange currentTimeRange = relevantTimeRanges.get(i);
        TimeRange nextTimeRange = (i+1) < relevantTimeRanges.size() ? relevantTimeRanges.get(i+1) : null;
        if (nextTimeRange != null) {
          isCondition = isNested(nextTimeRange, currentTimeRange);
        }
      }
      return isCondition;
    }
    return isCondition;
  }

  /** Deals with a condition given a string that describes the condition */
  private Collection<TimeRange> dealWithCondition(Collection<Event> events, Collection<String> attendees, String condition, long meetingDuration, int numberOfOptionalAttendeesAdded) {
    List<TimeRange> relevantTimeRanges = new ArrayList<TimeRange>();
    List<TimeRange> unorderedListOfTimeRanges = new ArrayList<TimeRange>();
    Collection<TimeRange> possibleMeetingTimes = new ArrayList<TimeRange>();
    // Get list of relevant time ranges.
    for (Event e : events) {
      for (String attendee : attendees) {
        if (e.getAttendees().contains(attendee)) {
          relevantTimeRanges.add(e.getWhen());
        }
      }
    }
    if ( relevantTimeRanges.isEmpty()) { 
      possibleMeetingTimes.add(wholeDay);
      return possibleMeetingTimes;
    }
    unorderedListOfTimeRanges.addAll(relevantTimeRanges);
    Collections.sort(relevantTimeRanges, TimeRange.ORDER_BY_START);
    TimeRange earliestEvent = relevantTimeRanges.get(0);
    TimeRange beforeFirstEvent = eventSplit(earliestEvent).get(0);
    if (enoughRoom(beforeFirstEvent, meetingDuration)) {
      possibleMeetingTimes.add(beforeFirstEvent);
    }
    if (condition == OVERLAP_CONDITION) {
      for (int i = 1; i < relevantTimeRanges.size(); i++) {
        TimeRange previousEvent = relevantTimeRanges.get(i-1);
        TimeRange currentEvent = relevantTimeRanges.get(i);
        if (previousEvent.overlaps(currentEvent)) {
          TimeRange afterCurrentEvent = eventSplit(currentEvent).get(1);
          if (enoughRoom(afterCurrentEvent, meetingDuration)) {
            possibleMeetingTimes.add(afterCurrentEvent);
          }
        }
      }
      return possibleMeetingTimes;
    }
    else if (condition == NESTED_CONDITION) {
      for (int i = 1; i < relevantTimeRanges.size(); i++) {
        TimeRange previousEvent = relevantTimeRanges.get(i-1);
        TimeRange currentEvent = relevantTimeRanges.get(i);
        if (isNested(currentEvent, previousEvent)) {
          TimeRange afterPreviousEvent = eventSplit(previousEvent).get(1);
          if (enoughRoom(afterPreviousEvent, meetingDuration)) {
            possibleMeetingTimes.add(afterPreviousEvent);
          }
        }
      }
      return possibleMeetingTimes;
    }
    else if (condition == NO_SPECIAL_CONDITION) {
      Collections.sort(relevantTimeRanges, TimeRange.ORDER_BY_END);
      return removePointEvents(considerEveryAttendee(relevantTimeRanges, meetingDuration, numberOfOptionalAttendeesAdded, unorderedListOfTimeRanges));
    }
    return null;
  }
  
  /** Checks if two events are nested */
  private boolean isNested(TimeRange currentEvent, TimeRange previousEvent) {
    boolean nested = false;
    if (currentEvent.start() > previousEvent.start() && currentEvent.end() < previousEvent.end()) {
          nested = true;
    }
    else if (currentEvent.start() == previousEvent.start() && currentEvent.end() < previousEvent.end()) {
          nested = true;
    }
    else if (currentEvent.start() > previousEvent.start() && currentEvent.end() == previousEvent.end()) {
        nested = true;
    }
    System.out.println("current event: " + previousEvent +" Next event: "+ currentEvent);
    return nested;
  }

  /** Removes point events */
  private Collection<TimeRange> removePointEvents(Collection<TimeRange> collectionOfTimeRanges) {
    Collection<TimeRange> noPointEventsIncluded = new ArrayList<TimeRange>();
    for (TimeRange t : collectionOfTimeRanges) {
      if (t.start() != t.end()) {
        noPointEventsIncluded.add(t);
      }
    }
    return noPointEventsIncluded;
  }

  /** Says  if free slot is large enough for meeting requested */
  private boolean enoughRoom(TimeRange freeSlot, long meetingDuration) {
    if (freeSlot.duration() >= meetingDuration) { return true; }
    else { return false; }
  }
}
