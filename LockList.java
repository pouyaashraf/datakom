package datakom;

/* this is basically a key-value pair linked list, ordered on offset */
public class LockList {
	
	/* finds the LockList matching offset, or null if none is found */
	public static LockList findByOffset(LockList l, int offset) {
		if(l == null) {
			return null;
		}
		while(l.offset < offset) {
			l = l.nextLockList;
		}
		if(l.offset == offset) {
			return l;
		}
		return null;
	}
	
	/* finds the first LockList with an offset greater than offset, or null */
	public static LockList findFirstAfter(LockList l, int offset) {
		while((l != null) && (l.offset <= offset)) {
			l = l.nextLockList;
		}
		return l;
	}
	
	private LockList nextLockList;
	private LockList previousLockList;
	private int offset;
	private int length;

	LockList(int offset, int length){
		this.nextLockList = null;
		this.previousLockList = null;
		this.offset = offset;
		this.length = length;	
	}

	LockList(int offset, int length, LockList previous){
		if(previous != null) {
			this.nextLockList = previous.nextLockList;
			this.previousLockList = previous;
			previous.nextLockList = this;
			if(previous.getNext() != null) {
				previous.nextLockList.previousLockList = this;
			}
		} else {
			nextLockList = previousLockList = null;
		}
		this.offset = offset;
		this.length = length;
	}

	public void remove(){
		if(previousLockList != null) {
			this.previousLockList.nextLockList = this.nextLockList;
		}
		if(nextLockList != null) {
			this.nextLockList.previousLockList = this.previousLockList;
		}
	}

	public int getOffset(){
		return this.offset;
	}

	public int getLength(){
		return this.length;
	}

	public LockList getNext(){
		return this.nextLockList;
	}

	public LockList getPrevious(){
		return this.previousLockList;
	}

	public void setOffset(int offset){
		this.offset = offset;
	}

	public void setLength(int length){
		this.length = length;
	}

}
