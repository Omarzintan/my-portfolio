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
import java.util.ArrayList;

public final class FindMeetingQuery {
  TimeRange wholeDay = TimeRange.WHOLE_DAY;
  int startDay = TimeRange.START_OF_DAY;
  int endDay = TimeRange.END_OF_DAY;

  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    // take care of No attendees
    Collection<String> attendees = request.getAttendees();
    Collection<TimeRange> setOfTimeRanges = new ArrayList<TimeRange>();

    if (attendees.isEmpty()) {
      setOfTimeRanges.add(wholeDay);
      return setOfTimeRanges;
    }
    // take care of requests too long
    long requestDuration = request.getDuration();
    if (requestDuration > wholeDay.duration()) { 
      return setOfTimeRanges;
    }
    //split day into two options
    Collection<TimeRange> tempTimeRanges = new ArrayList<TimeRange>();
    for (Event e : events) {
      TimeRange t = e.getWhen();
      tempTimeRanges.addAll(eventSplit(t));
    }
    Collection.sort(tempTimeRanges, TimeRange.ORDER_BY_START;
    return setOfTimeRanges;
  }

  private Collection<TimeRange> eventSplit(TimeRange timeRange) {
    Collection<TimeRange> setOfTimeRanges = new ArrayList<TimeRange>();
    TimeRange beforeEvent = TimeRange.fromStartEnd(startDay, timeRange.start(), false); 
    TimeRange afterEvent = TimeRange.fromStartEnd(timeRange.end(), endDay, true);
    setOfTimeRanges.add(beforeEvent);
    setOfTimeRanges.add(afterEvent);
    return setOfTimeRanges;
  }
}
