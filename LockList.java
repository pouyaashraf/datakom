package datakom;

/**
 * Basically a kay, value pair linked list. Ordered bu offset low > high.
 */
public class LockList {
	private LockList nextLockList;
	private LockList previousLockList;
	private int offset;
	private int length;
	
	/**
	 * constructs a new LockList node, case where no previous LockList is specified
	 *
	 * @param      offset  desired offset for new LockList node
	 * @param      length  desired length for new LockList node
	 */
	LockList(int offset, int length){
		this.nextLockList = null;
		this.previousLockList = null;
		this.offset = offset;
		this.length = length;	
	}

	/**
	 * constructs a new LockList node, case where a previously existing LockList is specified
	 *
	 * @param      offset    desired offset for new LickList node
	 * @param      length    desired length for the new LockList node
	 * @param      previous  previosly existing LockList.
	 */
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

	/**
	 * finds the LockList matching specified offset, null if non is found
	 *
	 * @param      l       A LockList (can be null)
	 * @param      offset  offset
	 *
	 * @return     returns object in list matching specified offset. 
	 * 				Null if not found/list is empty
	 */		
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
	
	/**
	 * Finds the first object in l with offset greater than specified offset, or null if l == null
	 *
	 * @param      l       a LockList (can be null)
	 * @param      offset  specified offset
	 *
	 * @return     object in list matching specified offset.
	 */
	public static LockList findFirstAfter(LockList l, int offset) {
		while((l != null) && (l.offset <= offset)) {
			l = l.nextLockList;
		}
		return l;
	}

	/**
	 * removes current node from LockList. 
	 * redirects previous and next to preserve correct structure
	 */
	public void remove(){
		if(previousLockList != null) {
			this.previousLockList.nextLockList = this.nextLockList;
		}
		if(nextLockList != null) {
			this.nextLockList.previousLockList = this.previousLockList;
		}
	}

	/**
	 *
	 *
	 * GETTERS AND SETTERS, A.K.A SELF DOCUMENTING CODE
	 * 
	 * 
	 */
	public int getOffset(){
		return this.offset;
	}

	public void setOffset(int offset){
		this.offset = offset;
	}

	public int getLength(){
		return this.length;
	}

	public void setLength(int length){
		this.length = length;
	}

	public LockList getNext(){
		return this.nextLockList;
	}

	public LockList getPrevious(){
		return this.previousLockList;
	}
}
