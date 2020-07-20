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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public final class FindMeetingQuery {
  /**
   * Taking a collection of events and a meeting request, returns a collection of time ranges 
   * that a meeting between all mandatory attendees could take place.
   * If one or more time slots where both mandatory and optional attendees could attend exist, 
   * return those instead.
   *
   * Events contain a title, a TimeRange, and a set of mandatory attendees.
   * MeetingRequests contain a duration and a set of mandatory/optional attendees, respectively. 
   */
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    long meetingDuration = request.getDuration();     // Length of the meeting to be scheduled.

    // If the request was longer than one day, return empty list.
    if (meetingDuration > (TimeRange.END_OF_DAY + 1)) {
      return Arrays.asList();
    }

    // If no other events were planned, return the whole day.
    if (events == null || events.isEmpty()) {
      return Arrays.asList(TimeRange.WHOLE_DAY);
    }

    Collection<String> mandatoryAttendees = request.getAttendees();
    Collection<String> optionalAttendees = request.getOptionalAttendees();

    // If there are no mandatory attendees, use only the optional attendees.
    if (mandatoryAttendees.isEmpty()) {
      return queryHelper(events, optionalAttendees, meetingDuration);
    }

    Collection<String> mandAndOpt = new HashSet<String>(mandatoryAttendees);
    mandAndOpt.addAll(optionalAttendees);

    Collection<TimeRange> timesForAll = queryHelper(events, mandAndOpt, meetingDuration);
    // If there are one or more time slots available for all potential attendees, return them.
    if (timesForAll.size() > 0) {
      return timesForAll;
    }
    else {
      return queryHelper(events, mandatoryAttendees, meetingDuration);
    }
}

/**
 * Contains the bulk of the meeting query algorithm.
 */
public Collection<TimeRange> queryHelper(Collection<Event> events, Collection<String> attendees, long duration) {
    ArrayList<TimeRange> takenTimes = new ArrayList<TimeRange>();   // A list of meeting times that are already taken.
    ArrayList<TimeRange> validTimes = new ArrayList<TimeRange>();   // A list of valid times to meet for all attendees.

    // Populate the taken meeting times list using the events param.
    // If no one in the planned event was a relevant attendee, don't add the time range to taken times.
    for (Event event: events) {
      if (!Collections.disjoint(attendees, event.getAttendees())) {
        takenTimes.add(event.getWhen());
      }
    }

    // If no times are taken at all, return the full day.
    if (takenTimes.isEmpty()) {
      validTimes.add(TimeRange.WHOLE_DAY);
      return validTimes;
    }

    // Sort the events by their start times and add the range from the beginning of the day to
    // the start of the first meeting if enough time exists.
    Collections.sort(takenTimes, TimeRange.ORDER_BY_START);
    int firstEventStart = takenTimes.get(0).start();
    if (firstEventStart >= duration) {
      validTimes.add(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, takenTimes.get(0).start(), false));
    }
    
    // Loop through the meetings (except the last) and add time ranges where no meetings exist.
    int currEndTime = takenTimes.get(0).end();   // holds the most relevant end time. 
    int currEventIndex = 0;                      // index of the event being compared in the loop--may not correspond to currEndTime.
    while (currEventIndex < (takenTimes.size() - 1)) {
      TimeRange currEvent = takenTimes.get(currEventIndex);
      TimeRange nextEvent = takenTimes.get(currEventIndex + 1);

      // If the current event overlaps the next event, it either contains the entire next event or its start.
      // Therefore, update only the end time as needed.
      // The proposed new end time must be greater than the current one.
      if (currEvent.overlaps(nextEvent) || currEndTime >= nextEvent.start()) { 
        int currEventEnd = currEvent.end();
        int nextEventEnd = nextEvent.end();
        if (currEvent.contains(nextEvent) && currEventEnd > currEndTime) {
          currEndTime = currEventEnd;
        }
        else if (nextEventEnd > currEndTime) {     // Next event ends after this event ends.
          currEndTime = nextEventEnd;
        }
      }
      // Else, we may have a new time range to add to available times.
      else {
        if (currEndTime + duration <= nextEvent.start()) {
          validTimes.add(TimeRange.fromStartDuration(currEndTime, (nextEvent.start() - currEndTime)));
          currEndTime = nextEvent.end();
        }
      }
      currEventIndex += 1;
    }
    
    // Add the range from the end of the last meeting of the day to the end of the day if enough time exists.
    int lastEventEnd = takenTimes.get(currEventIndex).end();
    // If, for example, the event with the penultimate start time contained the "last event" by start time, 
    // need to consider the penultimate meeting's end.
    if (currEndTime > lastEventEnd) {
      lastEventEnd = currEndTime;
    }
    if ((TimeRange.END_OF_DAY - lastEventEnd + 1) >= duration) {
      validTimes.add(TimeRange.fromStartEnd(lastEventEnd, TimeRange.END_OF_DAY, true));
    }

    return validTimes;
  }
}
