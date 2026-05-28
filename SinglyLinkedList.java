import java.util.NoSuchElementException;

public class SinglyLinkedList<E extends Comparable<E>>
{
	/* Fields */
	private ListNode<E> head, tail;		// head and tail pointers to list
	
	/* No-args Constructors */
	public SinglyLinkedList() {}
	
	/** Copy constructor */
	public SinglyLinkedList(SinglyLinkedList<E> oldList) {
		this.head = null;
		this.tail = null;

		if (oldList != null && oldList.getHead() != null) 
		{
			ListNode<E> current = oldList.getHead();
			while (current != null)
			{
				addLast(current.getValue());
				current = current.getNext();
			}
		}
	}
	
	/**	Clears the list of elements */
	public void clear() {
		head = null;
		tail = null;
	}
	
	/**	Add the object to the end of the list
	 *	@param obj		the object to add
	 *	@return			true if successful; false otherwise
	 */
	public boolean addLast(E obj) {
		ListNode<E> ls = new ListNode<>(obj);
		if (head == null)
		{
			head = ls;
			tail = ls;
		}
		else {
			tail.setNext(ls);
			tail = ls;
		}
		return true;
	}
	
	/**	Add the object to the beginning of the list
	 *	@param obj		the object to add
	 *	@return			true if successful; false otherwise
	 */
	public boolean addFirst(E obj) {
		ListNode<E> ls = new ListNode<>(obj);
		if (head == null)
		{
			head = ls;
			tail = ls;
		}
		else
		{
			ls.setNext(head);
			head = ls;
		}
		return true;
	}
	
	/**	@return		the head node */
	public ListNode<E> getHead() { return head; }
	
	/**	@return		the tail node */
	public ListNode<E> getTail() { return tail; }
		
	/**	@return the number of elements in this list */
	public int size() {
		int counter = 0;
		ListNode<E> current = head;
		while (current != null)
		{
			counter++;
			current = current.getNext();
		}
		return counter;
	}

	/**	Return the index of the first node that contains the obj
	 *	@param obj		the object to match
	 *	@return			if found, the index of the object; otherwise -1
	 */
	public int indexOf(E obj) {
		ListNode<E> current = head;
		int index = 0;
		while (current != null)
		{
			if (current.getValue().equals(obj))
			{
				return index;
			}
			current = current.getNext();
			index++;
		}
		return -1;
	}
	
	/**	Replace the object at the specified index
	 *	@param index		the index of the object
	 *	@param obj			the object that will replace the original
	 *	@return				the object that was replaced
	 *	@throws NoSuchElementException if index does not exist
	 */
	public E set(int index, E obj) {
		if (index < 0 || index >= size()) 
		{
    		throw new NoSuchElementException("Index: " + index + ", Size: " + size());
		}
		int pointer = 0;
		ListNode<E> current = head;
		while (pointer != index)
		{
			current = current.getNext();
			pointer++;
		}
		E value = current.getValue();
		current.setValue(obj);
		return value;
	}
	
	/**	Remove the element at the specified index
	 *	@param index		the index of the element
	 *	@return				the object in the element that was removed
	 *	@throws NoSuchElementException if index does not exist
	 */
	public E remove(int index) {
		if (index < 0 || index >= size()) 
		{
    		throw new NoSuchElementException("Index: " + index + ", Size: " + size());
		}
		if (index == 0) 
		{
			E removedValue = head.getValue();
			head = head.getNext();
			if (head == null) 
			{
				tail = null;
			}
			return removedValue;
		}	
		int pointer = 0;
		ListNode<E> current = head;
		while (pointer != index - 1)
		{
			current = current.getNext();
			pointer++;
		}
		ListNode<E> target = current.getNext();
		E value = target.getValue();
		current.setNext(target.getNext());
		
		if (current.getNext() == null)
		{
			tail = current;
		}
		return value;
	}
	
	/**	Add the object to the index of the list (middle)
	 *	@param index	the index in the list to add the obj
	 *	@param obj		the object to add
	 *	@return			true if successful; false otherwise
	 *	@throws NoSuchElementException if index < 0 or index > size()
	 */
	public boolean add(int index, E obj) {
		if (index < 0 || index > size()) 
		{
    		throw new NoSuchElementException("Index: " + index + ", Size: " + size());
		}
		if (index == 0) 
		{
			return addFirst(obj);
		}
		if (index == size())
		{
			return addLast(obj);
		}
		else
		{
			int pointer = 0;
			ListNode<E> current = head;
			while (pointer != index - 1)
			{
				current = current.getNext();
				pointer++;
			}
			ListNode<E> newNode = new ListNode<>(obj);
			newNode.setNext(current.getNext());
			current.setNext(newNode);
		}	
		return true;
	}
	
	/**	@return	true if list is empty; false otherwise */
	public boolean isEmpty() {
		return head == null;
	}
	
	/**	Tests whether the list contains the given object
	 *	@param object		the object to test
	 *	@return				true if the object is in the list; false otherwise
	 */
	public boolean contains(E object) {
		return indexOf(object) != -1;
	}
	
	/**	toString method */
	public String toString()
	{
		ListNode<E> ptr = head;
		String str = "";
		while (ptr != null)
		{
			str += ptr.getValue() + " ";
			ptr = ptr.getNext();
		}
		return str;
	}
}