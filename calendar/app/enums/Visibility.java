package enums;

/**
 * Provides three layers of visibility to control the privacy of Events.
 */
public enum Visibility {
	/**
	 * All Users are allowed to see this Event.
	 */
	PUBLIC,
	/**
	 * All Users are allowed to see this Events start and end date, but nothing more.
	 */
	BUSY,
	/**
	 * Only the User who created this Event is allowed to see it.
	 */
	PRIVATE
}