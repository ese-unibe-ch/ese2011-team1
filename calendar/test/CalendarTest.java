import java.util.LinkedList;

import models.Calendar;
import models.Database;
import models.Event;
import models.PointEvent;
import models.RepeatingEvent;
import models.User;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;
import enums.Interval;
import enums.Visibility;

public class CalendarTest extends UnitTest {
	private User owner;
	private User francis;
	private Calendar calendarOfOwner;
	// private Calendar calendarOfFrancis;
	private PointEvent event;
	private RepeatingEvent repeatingEvent1;
	private RepeatingEvent repeatingEvent2;
	private Event eventToday;
	private Event eventToday2;
	private Calendar francisCalendar;

	@Before
	public void setUp() throws Exception {
		this.owner = new User("hans", "123", new DateTime(), "hans1",Database.messageSystem);
		this.francis = new User("Francis", "1234", new DateTime(), "francis", Database.messageSystem);
		this.calendarOfOwner = new Calendar("Calendar", this.owner);
		this.francisCalendar = new Calendar("FrancisCal", francis);
		this.event = new PointEvent("anEvent", new DateTime(0),
				new DateTime(0), Visibility.PRIVATE, calendarOfOwner);
		this.eventToday = new PointEvent("eventToday", new DateTime(),
				new DateTime(), Visibility.PRIVATE, calendarOfOwner);
		this.eventToday2 = new PointEvent("eventToday2", new DateTime(),
				new DateTime(), Visibility.PUBLIC, francisCalendar);
		this.repeatingEvent1 = new RepeatingEvent("repeatingEvent",
				new DateTime(0), new DateTime(0), Visibility.PRIVATE,
				calendarOfOwner, Interval.WEEKLY);
		this.repeatingEvent1.init();
		this.repeatingEvent2 = new RepeatingEvent("repeatingEvent",
				new DateTime(), new DateTime(), Visibility.PRIVATE,
				calendarOfOwner, Interval.MONTHLY);
		this.repeatingEvent2.init();
	}

	@Test
	public void testToString() {
		long calendarId = calendarOfOwner.getId();
		assertEquals("Calendar [" + calendarId + "]",
				calendarOfOwner.toString());
	}

	@Test
	public void testSetOwner() {
		assertEquals(owner, calendarOfOwner.getOwner());
		calendarOfOwner.setOwner(francis);
		assertEquals(francis, calendarOfOwner.getOwner());
	}

	@Test
	public void testSetName() {
		assertEquals("Calendar", calendarOfOwner.getName());
		calendarOfOwner.setName("CalendarChanged");
		assertEquals("CalendarChanged", calendarOfOwner.getName());
	}

	@Test
	public void testGetOwner() {
		assertEquals(owner, calendarOfOwner.getOwner());
	}

	@Test
	public void testGetName() {
		assertEquals("Calendar", calendarOfOwner.getName());
	}

	@Test
	public void testGetId() {
		long id = calendarOfOwner.getId();
		assertEquals(id, calendarOfOwner.getId());
	}

	@Test
	public void testGetHeadById() {
		long id = event.getId();
		assertNull(calendarOfOwner.getHeadById(id));
		calendarOfOwner.addEvent(event);
		assertEquals(event, calendarOfOwner.getHeadById(id));
	}

	@Test
	public void testGetHeadByOriginId() {
		long originId = repeatingEvent1.getOriginId();
		assertTrue(calendarOfOwner.getHeadsByOriginId(originId).isEmpty());
		calendarOfOwner.addEvent(repeatingEvent1);
		assertTrue(calendarOfOwner.getHeadsByOriginId(originId).contains(
				repeatingEvent1));
		assertFalse(calendarOfOwner.getHeadsByOriginId(originId).contains(
				repeatingEvent1.getNextReference()));
	}

	@Test
	public void testGetEventHeads() {
		calendarOfOwner.addEvent(event);
		assertTrue(calendarOfOwner.getEventHeads().contains(event));
	}

	@Test
	public void testGetSameBaseIdEvents() {
		long baseId = repeatingEvent1.getBaseId();
		assertTrue(calendarOfOwner.getSameBaseIdEvents(baseId).isEmpty());
		calendarOfOwner.addEvent(repeatingEvent1);
		assertTrue(calendarOfOwner.getSameBaseIdEvents(baseId).contains(
				repeatingEvent1));
		assertTrue(calendarOfOwner.getSameBaseIdEvents(baseId).contains(
				repeatingEvent1.getNextReference()));
		// NOTE: to avoid generating unnecessary Events, only the first and
		// second are generated on instantiation.
	}

	@Test
	public void testAddEvent() {
		assertTrue(calendarOfOwner.getEventHeads().isEmpty());
		calendarOfOwner.addEvent(event);
		assertFalse(calendarOfOwner.getEventHeads().isEmpty());
		assertTrue(calendarOfOwner.getEventHeads().contains(event));
	}

	@Test
	public void testGetEventById() {
		long id = event.getId();
		assertNull(calendarOfOwner.getEventById(id));
		calendarOfOwner.addEvent(event);
		assertEquals(event, calendarOfOwner.getEventById(id));
	}

	@Test
	public void testGetDayEvents() {
		calendarOfOwner.addEvent(event);
		calendarOfOwner.addEvent(eventToday);
		calendarOfOwner.addEvent(eventToday2);
		LinkedList<Event> DayEvents = calendarOfOwner.getEventsOfDate(
				new LocalDate(), owner);
		LinkedList<Event> DayEventsFrancis = calendarOfOwner.getEventsOfDate(
				new LocalDate(), francis);
		assertFalse(DayEvents.isEmpty());
		assertFalse(DayEventsFrancis.isEmpty());
		assertEquals("[eventToday, eventToday2]", DayEvents.toString());
		assertEquals("[eventToday2]", DayEventsFrancis.toString());
	}

	@Test
	public void testHasEventOnDate() {
		DateTime testDate = eventToday.getStart();
		assertTrue(eventToday.happensOn(testDate.toLocalDate()));
		assertFalse(calendarOfOwner.hasEventOnDate(testDate.toLocalDate(),
				owner));
		calendarOfOwner.addEvent(eventToday);
		assertTrue(calendarOfOwner
				.hasEventOnDate(testDate.toLocalDate(), owner));
	}

	@Test
	public void testHasEventOnDateIncludingObserved() {
		eventToday2.setVisiblility(Visibility.PRIVATE);
		DateTime testDate = eventToday.getStart();
		owner.addObservedCalendar(francisCalendar);
		owner.addShownObservedCalendar(francisCalendar.getId());
		assertTrue(eventToday.happensOn(testDate.toLocalDate()));
		assertTrue(eventToday2.happensOn(testDate.toLocalDate()));
		assertFalse(calendarOfOwner.hasEventOnDateIncludingObserved(
				testDate.getDayOfMonth(), testDate.getMonthOfYear(),
				testDate.getYear(), owner));
		calendarOfOwner.addEvent(eventToday);
		francisCalendar.addEvent(eventToday2);
		assertTrue(calendarOfOwner.hasEventOnDateIncludingObserved(
				testDate.getDayOfMonth(), testDate.getMonthOfYear(),
				testDate.getYear(), owner));
		eventToday2.setVisiblility(Visibility.PUBLIC);
		assertTrue(calendarOfOwner.hasEventOnDateIncludingObserved(
				testDate.getDayOfMonth(), testDate.getMonthOfYear(),
				testDate.getYear(), owner));
		calendarOfOwner.removeEvent(eventToday.getId());
		assertTrue(calendarOfOwner.hasEventOnDateIncludingObserved(
				testDate.getDayOfMonth(), testDate.getMonthOfYear(),
				testDate.getYear(), owner));
		eventToday2.setVisiblility(Visibility.PRIVATE);
		assertFalse(calendarOfOwner.hasEventOnDateIncludingObserved(
				testDate.getDayOfMonth(), testDate.getMonthOfYear(),
				testDate.getYear(), owner));
	}

	@Test
	public void testAddRepeatingEvent() {
		assertTrue(calendarOfOwner.getEventHeads().isEmpty());
		calendarOfOwner.addEvent(repeatingEvent1);
		assertFalse(calendarOfOwner.getEventHeads().isEmpty());
		assertTrue(calendarOfOwner.getEventHeads().contains(repeatingEvent1));
	}

	@Test
	public void testRemoveEvent() {
		calendarOfOwner.addEvent(event);
		assertTrue(calendarOfOwner.getEventHeads().contains(event));
		calendarOfOwner.removeEvent(event.getId());
		assertTrue(calendarOfOwner.getEventHeads().isEmpty());
		assertFalse(calendarOfOwner.getEventHeads().contains(event));
	}

	@Test
	public void testRemoveRepeatedEvents() {
		assertTrue(calendarOfOwner.getEventHeads().isEmpty());
		Event repeating1 = repeatingEvent1.getNextReference();
		Event repeating2 = repeating1.getNextReference();
		Event repeating3 = repeating2.getNextReference();
		assertEquals(repeatingEvent1.getBaseId(), repeating1.getBaseId());
		assertEquals(repeating1.getBaseId(), repeating2.getBaseId());
		assertEquals(repeating2.getBaseId(), repeating3.getBaseId());
		calendarOfOwner.addEvent(repeatingEvent1);
		calendarOfOwner.addEvent(repeating1);
		calendarOfOwner.addEvent(repeating2);
		calendarOfOwner.addEvent(repeating3);
		assertFalse(calendarOfOwner.getEventHeads().isEmpty());
		assertTrue(calendarOfOwner.getEventHeads().contains(repeating1));
		assertTrue(calendarOfOwner.getEventHeads().contains(repeating2));
		assertTrue(calendarOfOwner.getEventHeads().contains(repeating3));
	}

	@Test
	public void testGenerateNextEventsForOneEvent() {
		User owner = calendarOfOwner.getOwner();
		DateTime repeatingStart = repeatingEvent1.getStart();
		calendarOfOwner.addEvent(repeatingEvent1);
		calendarOfOwner.generateNextEvents(repeatingEvent1, repeatingStart);
		DateTime nextRepetitionDate = repeatingStart.plusDays(repeatingEvent1
				.getInterval().getDays());
		Event nextRepetition = repeatingEvent1.getNextReference();
		assertTrue(nextRepetition.happensOn(nextRepetitionDate.toLocalDate()));
		assertTrue(nextRepetition.getStart().equals(
				repeatingStart
						.plusDays(repeatingEvent1.getInterval().getDays())));
		assertTrue(calendarOfOwner.hasEventOnDate(
				nextRepetitionDate.toLocalDate(), owner));
		assertTrue(calendarOfOwner.getEventsOfDate(
				nextRepetitionDate.toLocalDate(), owner).contains(
				nextRepetition));
	}

	@Test
	public void testGenerateAllNextEvents() {
		User owner = calendarOfOwner.getOwner();
		DateTime repeatingStart = repeatingEvent1.getStart();
		DateTime repeating2Start = repeatingEvent2.getStart();
		calendarOfOwner.addEvent(repeatingEvent1);
		calendarOfOwner.addEvent(repeatingEvent2);
		calendarOfOwner.generateAllNextEvents(repeatingStart);
		DateTime nextRepetitionDate1 = repeatingStart.plusDays(repeatingEvent1
				.getInterval().getDays());
		DateTime nextRepetitionDate2 = repeating2Start.plusMonths(1);
		Event nextRepetition1 = repeatingEvent1.getNextReference();
		Event nextRepetition2 = repeatingEvent2.getNextReference();
		assertTrue(calendarOfOwner.hasEventOnDate(
				nextRepetitionDate1.toLocalDate(), owner));
		assertTrue(calendarOfOwner.getEventsOfDate(
				nextRepetitionDate1.toLocalDate(), owner).contains(
				nextRepetition1));
		assertTrue(calendarOfOwner.hasEventOnDate(
				nextRepetitionDate2.toLocalDate(), owner));
		assertTrue(calendarOfOwner.getEventsOfDate(
				nextRepetitionDate2.toLocalDate(), owner).contains(
				nextRepetition2));
	}

	@Test
	public void testGetAllEventsOfDate() {
		eventToday2.setVisiblility(Visibility.PRIVATE);
		calendarOfOwner.addEvent(eventToday);
		francisCalendar.addEvent(eventToday2);
		owner.addObservedCalendar(francisCalendar);
		owner.addShownObservedCalendar(francisCalendar.getId());
		DateTime testDate = eventToday.getStart();
		assertTrue(eventToday2.isPrivate());
		assertTrue(calendarOfOwner.getAllVisibleEventsOfDate(
				testDate.getDayOfMonth(), testDate.getMonthOfYear(),
				testDate.getYear(), owner).contains(eventToday));
		assertFalse(calendarOfOwner.getAllVisibleEventsOfDate(
				testDate.getDayOfMonth(), testDate.getMonthOfYear(),
				testDate.getYear(), owner).contains(eventToday2));
		eventToday2.setVisiblility(Visibility.PUBLIC);
		assertTrue(calendarOfOwner.getAllVisibleEventsOfDate(
				testDate.getDayOfMonth(), testDate.getMonthOfYear(),
				testDate.getYear(), owner).contains(eventToday));
		assertTrue(calendarOfOwner.getAllVisibleEventsOfDate(
				testDate.getDayOfMonth(), testDate.getMonthOfYear(),
				testDate.getYear(), owner).contains(eventToday2));
	}

	@Test
	public void testEditEvent() {
		String newName = "newName";
		DateTime newStart = new DateTime(5000000);
		DateTime newEnd = new DateTime(6000000);
		Visibility newVisibility = Visibility.BUSY;
		Interval newInterval = Interval.NONE;
		DateTime newFrom = new DateTime(5500000);
		DateTime newTo = new DateTime(6600000);
		String newDescription = "new Description";
		calendarOfOwner.editEvent(event, newName, newStart, newEnd,
				newVisibility, newInterval, newFrom, newTo, newDescription);
		assertEquals(newName, event.getName());
		assertEquals(newStart, event.getStart());
		assertEquals(newEnd, event.getEnd());
		assertEquals(newVisibility, event.getVisibility());
		assertEquals(newDescription, event.getDescription());
	}

	@Test
	public void testRemoveSeriesOfRepeatingEvents() {
		DateTime startDate = repeatingEvent1.getStart();
		calendarOfOwner.addEvent(repeatingEvent1);

		repeatingEvent1.generateNextEvents(startDate);
		assertTrue(calendarOfOwner.hasEventOnDate(startDate.toLocalDate(),
				owner));
		Event repetition1 = repeatingEvent1.getNextReference();
		assertTrue(calendarOfOwner.hasEventOnDate(repetition1.getStart()
				.toLocalDate(), owner));
		Event repetition2 = repetition1.getNextReference();
		assertTrue(calendarOfOwner.hasEventOnDate(repetition2.getStart()
				.toLocalDate(), owner));
		assertTrue(calendarOfOwner.getSameBaseIdEvents(
				repeatingEvent1.getBaseId()).contains(repeatingEvent1));
		assertTrue(calendarOfOwner.getSameBaseIdEvents(repetition1.getBaseId())
				.contains(repeatingEvent1));
		assertTrue(calendarOfOwner.getSameBaseIdEvents(repetition2.getBaseId())
				.contains(repeatingEvent1));
		calendarOfOwner.removeSeriesOfRepeatingEvents(repeatingEvent1);
		assertTrue(calendarOfOwner.getSameBaseIdEvents(
				repeatingEvent1.getBaseId()).isEmpty());
		assertFalse(calendarOfOwner.hasEventOnDate(startDate.toLocalDate(),
				owner));
		assertFalse(calendarOfOwner.hasEventOnDate(repetition1.getStart()
				.toLocalDate(), owner));
		assertFalse(calendarOfOwner.hasEventOnDate(repetition2.getStart()
				.toLocalDate(), owner));
	}

	@Test
	public void testCancelRepeatingEventRepetitionInEndlessIterval() {

		// set up some repetitions
		DateTime startDate = repeatingEvent1.getStart();
		calendarOfOwner.addEvent(repeatingEvent1);
		calendarOfOwner.generateNextEvents(repeatingEvent1,
				startDate.plusMonths(3));
		assertTrue(calendarOfOwner.hasEventOnDate(startDate.toLocalDate(),
				owner));
		Event repetition1 = repeatingEvent1;
		assertTrue(calendarOfOwner.hasEventOnDate(repetition1.getStart()
				.toLocalDate(), owner));
		Event repetition2 = repetition1.getNextReference();
		assertTrue(calendarOfOwner.hasEventOnDate(repetition2.getStart()
				.toLocalDate(), owner));
		Event repetition3 = repetition2.getNextReference();
		assertTrue(calendarOfOwner.hasEventOnDate(repetition3.getStart()
				.toLocalDate(), owner));
		Event repetition4 = repetition3.getNextReference();
		assertTrue(calendarOfOwner.hasEventOnDate(repetition4.getStart()
				.toLocalDate(), owner));
		Event repetition5 = repetition4.getNextReference();
		assertTrue(calendarOfOwner.hasEventOnDate(repetition5.getStart()
				.toLocalDate(), owner));
		Event repetition6 = repetition5.getNextReference();
		assertTrue(calendarOfOwner.hasEventOnDate(repetition6.getStart()
				.toLocalDate(), owner));
		Event repetition7 = repetition6.getNextReference();
		assertTrue(calendarOfOwner.hasEventOnDate(repetition7.getStart()
				.toLocalDate(), owner));
		assertTrue(calendarOfOwner.hasEventOnDate(repeatingEvent1.getStart()
				.toLocalDate(), owner));
		assertTrue(calendarOfOwner.hasEventOnDate(repetition1.getStart()
				.toLocalDate(), owner));
		assertTrue(calendarOfOwner.hasEventOnDate(repetition2.getStart()
				.toLocalDate(), owner));
		assertTrue(calendarOfOwner.hasEventOnDate(repetition3.getStart()
				.toLocalDate(), owner));
		assertTrue(calendarOfOwner.hasEventOnDate(repetition4.getStart()
				.toLocalDate(), owner));
		assertTrue(calendarOfOwner.hasEventOnDate(repetition5.getStart()
				.toLocalDate(), owner));
		assertTrue(calendarOfOwner.hasEventOnDate(repetition6.getStart()
				.toLocalDate(), owner));
		assertTrue(calendarOfOwner.hasEventOnDate(repetition7.getStart()
				.toLocalDate(), owner));

		// first, cancel somewhere in repeating Event
		calendarOfOwner.cancelRepeatingEventRepetitionFromDate(repetition5);
		assertTrue(calendarOfOwner.hasEventOnDate(repetition1.getStart()
				.toLocalDate(), owner));
		assertTrue(calendarOfOwner.hasEventOnDate(repetition2.getStart()
				.toLocalDate(), owner));
		assertTrue(calendarOfOwner.hasEventOnDate(repetition3.getStart()
				.toLocalDate(), owner));
		assertTrue(calendarOfOwner.hasEventOnDate(repetition4.getStart()
				.toLocalDate(), owner));
		assertTrue(calendarOfOwner.hasEventOnDate(repetition5.getStart()
				.toLocalDate(), owner));
		assertFalse(calendarOfOwner.hasEventOnDate(repetition6.getStart()
				.toLocalDate(), owner));
		assertFalse(calendarOfOwner.hasEventOnDate(repetition7.getStart()
				.toLocalDate(), owner));

	}

	@Test
	public void testCancelRepeatingEventRepetitionInInterval() {
		// set up some repetitions
		DateTime startDate = repeatingEvent1.getStart();
		calendarOfOwner.addEvent(repeatingEvent1);
		calendarOfOwner.generateNextEvents(repeatingEvent1,
				startDate.plusMonths(3));
		assertTrue(calendarOfOwner.hasEventOnDate(startDate.toLocalDate(),
				owner));
		Event repetition1 = repeatingEvent1;
		assertTrue(calendarOfOwner.hasEventOnDate(repetition1.getStart()
				.toLocalDate(), owner));
		Event repetition2 = repetition1.getNextReference();
		assertTrue(calendarOfOwner.hasEventOnDate(repetition2.getStart()
				.toLocalDate(), owner));
		Event repetition3 = repetition2.getNextReference();
		assertTrue(calendarOfOwner.hasEventOnDate(repetition3.getStart()
				.toLocalDate(), owner));
		Event repetition4 = repetition3.getNextReference();
		assertTrue(calendarOfOwner.hasEventOnDate(repetition4.getStart()
				.toLocalDate(), owner));
		Event repetition5 = repetition4.getNextReference();
		assertTrue(calendarOfOwner.hasEventOnDate(repetition5.getStart()
				.toLocalDate(), owner));
		Event repetition6 = repetition5.getNextReference();
		assertTrue(calendarOfOwner.hasEventOnDate(repetition6.getStart()
				.toLocalDate(), owner));
		Event repetition7 = repetition6.getNextReference();
		assertTrue(calendarOfOwner.hasEventOnDate(repetition7.getStart()
				.toLocalDate(), owner));
		assertTrue(calendarOfOwner.hasEventOnDate(repeatingEvent1.getStart()
				.toLocalDate(), owner));
		assertTrue(calendarOfOwner.hasEventOnDate(repetition1.getStart()
				.toLocalDate(), owner));
		assertTrue(calendarOfOwner.hasEventOnDate(repetition2.getStart()
				.toLocalDate(), owner));
		assertTrue(calendarOfOwner.hasEventOnDate(repetition3.getStart()
				.toLocalDate(), owner));
		assertTrue(calendarOfOwner.hasEventOnDate(repetition4.getStart()
				.toLocalDate(), owner));
		assertTrue(calendarOfOwner.hasEventOnDate(repetition5.getStart()
				.toLocalDate(), owner));
		assertTrue(calendarOfOwner.hasEventOnDate(repetition6.getStart()
				.toLocalDate(), owner));
		assertTrue(calendarOfOwner.hasEventOnDate(repetition7.getStart()
				.toLocalDate(), owner));

		// second, cancel somewhere in the above generated interval (from
		// repeatingEvent1 to repetition5)
		calendarOfOwner.cancelRepeatingEventRepetitionFromDate(repetition3);
		assertTrue(calendarOfOwner.hasEventOnDate(repetition1.getStart()
				.toLocalDate(), owner));
		assertTrue(calendarOfOwner.hasEventOnDate(repetition2.getStart()
				.toLocalDate(), owner));
		assertTrue(calendarOfOwner.hasEventOnDate(repetition3.getStart()
				.toLocalDate(), owner));
		assertFalse(calendarOfOwner.hasEventOnDate(repetition4.getStart()
				.toLocalDate(), owner));
		assertFalse(calendarOfOwner.hasEventOnDate(repetition5.getStart()
				.toLocalDate(), owner));
		assertFalse(calendarOfOwner.hasEventOnDate(repetition6.getStart()
				.toLocalDate(), owner));
		assertFalse(calendarOfOwner.hasEventOnDate(repetition7.getStart()
				.toLocalDate(), owner));

	}

	@Test
	public void testCancelRepeatingEventRepetitionForHead() {
		// set up some repetitions
		DateTime startDate = repeatingEvent1.getStart();
		calendarOfOwner.addEvent(repeatingEvent1);
		calendarOfOwner.generateNextEvents(repeatingEvent1,
				startDate.plusMonths(3));
		assertTrue(calendarOfOwner.hasEventOnDate(startDate.toLocalDate(),
				owner));
		Event repetition1 = repeatingEvent1;
		assertTrue(calendarOfOwner.hasEventOnDate(repetition1.getStart()
				.toLocalDate(), owner));
		Event repetition2 = repetition1.getNextReference();
		assertTrue(calendarOfOwner.hasEventOnDate(repetition2.getStart()
				.toLocalDate(), owner));
		Event repetition3 = repetition2.getNextReference();
		assertTrue(calendarOfOwner.hasEventOnDate(repetition3.getStart()
				.toLocalDate(), owner));
		Event repetition4 = repetition3.getNextReference();
		assertTrue(calendarOfOwner.hasEventOnDate(repetition4.getStart()
				.toLocalDate(), owner));
		Event repetition5 = repetition4.getNextReference();
		assertTrue(calendarOfOwner.hasEventOnDate(repetition5.getStart()
				.toLocalDate(), owner));
		Event repetition6 = repetition5.getNextReference();
		assertTrue(calendarOfOwner.hasEventOnDate(repetition6.getStart()
				.toLocalDate(), owner));
		Event repetition7 = repetition6.getNextReference();
		assertTrue(calendarOfOwner.hasEventOnDate(repetition7.getStart()
				.toLocalDate(), owner));
		assertTrue(calendarOfOwner.hasEventOnDate(repeatingEvent1.getStart()
				.toLocalDate(), owner));
		assertTrue(calendarOfOwner.hasEventOnDate(repetition1.getStart()
				.toLocalDate(), owner));
		assertTrue(calendarOfOwner.hasEventOnDate(repetition2.getStart()
				.toLocalDate(), owner));
		assertTrue(calendarOfOwner.hasEventOnDate(repetition3.getStart()
				.toLocalDate(), owner));
		assertTrue(calendarOfOwner.hasEventOnDate(repetition4.getStart()
				.toLocalDate(), owner));
		assertTrue(calendarOfOwner.hasEventOnDate(repetition5.getStart()
				.toLocalDate(), owner));
		assertTrue(calendarOfOwner.hasEventOnDate(repetition6.getStart()
				.toLocalDate(), owner));
		assertTrue(calendarOfOwner.hasEventOnDate(repetition7.getStart()
				.toLocalDate(), owner));

		// third, Cancel from repeatingEvent1
		calendarOfOwner.cancelRepeatingEventRepetitionFromDate(repeatingEvent1);
		assertTrue(calendarOfOwner.hasEventOnDate(repetition1.getStart()
				.toLocalDate(), owner));
		assertFalse(calendarOfOwner.hasEventOnDate(repetition2.getStart()
				.toLocalDate(), owner));
		assertFalse(calendarOfOwner.hasEventOnDate(repetition3.getStart()
				.toLocalDate(), owner));
		assertFalse(calendarOfOwner.hasEventOnDate(repetition4.getStart()
				.toLocalDate(), owner));
		assertFalse(calendarOfOwner.hasEventOnDate(repetition5.getStart()
				.toLocalDate(), owner));
		assertFalse(calendarOfOwner.hasEventOnDate(repetition6.getStart()
				.toLocalDate(), owner));
		assertFalse(calendarOfOwner.hasEventOnDate(repetition7.getStart()
				.toLocalDate(), owner));
		assertFalse(calendarOfOwner.getEventHeads().contains(repetition7));
	}
	
	@Test
	public void testSearchEvent() {
		calendarOfOwner.addEvent(event);
		System.out.println("avafdv: " + calendarOfOwner.searchEvent("", owner, event.getStart()));
		assertTrue(calendarOfOwner.searchEvent(event.getName(), owner, event.getStart()).contains(event));
		assertTrue(calendarOfOwner.searchEvent("", owner, event.getStart()).contains(event));
		assertTrue(calendarOfOwner.searchEvent("ev", owner, event.getStart()).contains(event));
		event.editDescription("danidanidanidani");
		assertTrue(calendarOfOwner.searchEvent("ni", owner, event.getStart()).contains(event));
		
	}

}
