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
  TimeRange wholeDay = TimeRange.WHOLE_DAY;
  int startDay = TimeRange.START_OF_DAY;
  int endDay = TimeRange.END_OF_DAY;

  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    // take care of No attendees
    Collection<String> attendees = request.getAttendees();
    Collection<TimeRange> setOfTimeRanges = new ArrayList<TimeRange>();
    List<TimeRange> tempTimeRanges = new ArrayList<TimeRange>();
    Collection<TimeRange> possibleMeetingTimes = new ArrayList<TimeRange>();

    if (attendees.isEmpty()) {
      setOfTimeRanges.add(wholeDay);
      return setOfTimeRanges;
    }

    // take care of requests too long
    long requestDuration = request.getDuration();
    if (requestDuration > wholeDay.duration()) { 
      return setOfTimeRanges;
    }
    
    for (Event e : events) {
      TimeRange t = e.getWhen();
      tempTimeRanges.add(t);
    }
    
    // if (overlapExits(events, attendees));
    if (overlapExits(events, attendees)){
      return dealWithOverlaps(tempTimeRanges);
    }
    Collections.sort(tempTimeRanges, TimeRange.ORDER_BY_END);
    possibleMeetingTimes = considerEveryAttendee(tempTimeRanges);
    for (TimeRange t : possibleMeetingTimes) { setOfTimeRanges.add(t); }
    return setOfTimeRanges;
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
  private Collection<TimeRange> considerEveryAttendee(List<TimeRange> orderedListOfTimeRanges) {
    Collection<TimeRange> results = new ArrayList<TimeRange>();

    // take care of corner case where there is only one event
    if (orderedListOfTimeRanges.size() == 1) {
      TimeRange onlyEvent = orderedListOfTimeRanges.get(0);
      results.add(eventSplit(onlyEvent).get(0));
      results.add(eventSplit(onlyEvent).get(1));
      return results;
    }

    //take the earliest event
    TimeRange firstEvent = orderedListOfTimeRanges.get(0);
    
    // split free time into before and after the event
    TimeRange beforeFirstEvent = eventSplit(firstEvent).get(0);
    TimeRange afterFirstEvent = eventSplit(firstEvent).get(1);
    
    //let first free time be before the first event;
    results.add(beforeFirstEvent);

    //loop starting from next event
    for (int i = 1; i < orderedListOfTimeRanges.size(); i++) {
      TimeRange currentEvent = orderedListOfTimeRanges.get(i);
      if (afterFirstEvent.overlaps(currentEvent)) {
        TimeRange nextFreeSlot = TimeRange.fromStartEnd(afterFirstEvent.start(), currentEvent.start(), false);
        afterFirstEvent = TimeRange.fromStartEnd(currentEvent.end(), afterFirstEvent.end(), false);
        results.add(nextFreeSlot);
      }
    }
    
    //for remaining freeSlot that has no clashes
    TimeRange nextFreeSlot = afterFirstEvent;
    results.add(nextFreeSlot);
    
    return results;
  } 

  /** Checks for any overlapping events */
  private boolean overlapExits(Collection<Event> events, Collection<String> attendees) {
    List<TimeRange> relevantTimeRanges = new ArrayList<TimeRange>();
    for (Event e : events) {
      for (String attendee : attendees) {
        if (e.getAttendees().contains(attendee)) {
          relevantTimeRanges.add(e.getWhen());
        }
      }
    }
    Collections.sort(relevantTimeRanges, TimeRange.ORDER_BY_START);
    boolean overlap = false;
    for (int i = 0; i < relevantTimeRanges.size(); i++) {
      TimeRange currentTimeRange = relevantTimeRanges.get(i);
      TimeRange nextTimeRange = (i+1) < relevantTimeRanges.size() ? relevantTimeRanges.get(i+1) : null;
      if (nextTimeRange != null && currentTimeRange.overlaps(nextTimeRange)) {
        overlap = true;
        System.out.println("current event: " + currentTimeRange +" Next event: "+ nextTimeRange);
        return overlap;
      }
    }
    return overlap;
  } 

  /** Deals with overlapping events */
  private Collection<TimeRange> dealWithOverlaps(List<TimeRange> listOfTimeRanges){
    Collection<TimeRange> possibleMeetingTimes = new ArrayList<TimeRange>();
    // order list by start
    Collections.sort(listOfTimeRanges, TimeRange.ORDER_BY_START);
    // get earliest event
    TimeRange earliestEvent = listOfTimeRanges.get(0);
    // split earliest event to get its before and after
    TimeRange beforeFirstEvent = eventSplit(earliestEvent).get(0);
    possibleMeetingTimes.add(beforeFirstEvent);
    for (int i = 1; i < listOfTimeRanges.size(); i++) {
      TimeRange previousEvent = listOfTimeRanges.get(i-1);
      TimeRange currentEvent = listOfTimeRanges.get(i);
      if (previousEvent.overlaps(currentEvent)) {
        TimeRange afterCurrentEvent = eventSplit(currentEvent).get(1);
        possibleMeetingTimes.add(afterCurrentEvent);
      }
    }
    return possibleMeetingTimes;
  }
}
