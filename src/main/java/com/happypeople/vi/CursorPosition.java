package com.happypeople.vi;

/** This models the fairly simple x y coordinates, used as the position of a cursor.
 * @param <E> The concrete subtype of this class
 */
public abstract class CursorPosition<E extends CursorPosition<E>> {
	private final long x;
	private final long y;

	public CursorPosition(final long x, final long y) {
		this.x=x;
		this.y=y;
	}

	public long getX() {
		return x;
	}

	public long getY() {
		return y;
	}

	public abstract E addX(long dX);
	public abstract E addY(long dY);

	/** Relativ movement of Cursor
	 * @param d moving delta
	 * @return new Cursor position created by calls to addX() and addY()
	 */
	public E add(final CursorPosition<E> d) {
		return addX(d.getX()).addY(d.getY());
	}

	public E setX(final long x) {
		return addX(x-getX());
	}

	public E setY(final long y) {
		return addY(y-getY());
	}

	@Override
	public boolean equals(final Object other) {
		return	other!=null &&
				other instanceof CursorPosition &&
				getX()==((CursorPosition<?>)other).getX() &&
				getY()==((CursorPosition<?>)other).getY();
	}

	@Override
	public int hashCode() {
		return Long.valueOf(x+y).hashCode();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName()+": x="+getX()+" y="+getY();
	}
}
