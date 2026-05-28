import java.util.NoSuchElementException;


public class SinglyLinkedListTester
{
	private SinglyLinkedList<Coordinate> sll;
	
	private final int NUM_COORDS = 3;
	
	public SinglyLinkedListTester() { sll = new SinglyLinkedList<Coordinate>(); }
	
	public static void main(String[] args)
	{
		SinglyLinkedListTester sllt = new SinglyLinkedListTester();
		sllt.run();
	}
	
	public void run()
	{
		System.out.println("\nSingly Linked List Tester\n-------------------------");
		testAddLast();				// Assignment 1
		testAddFirst();				// Assignment 2
		testClear();				// Assignment 3
		testGetHeadTail();			// Assignment 4
		testSize();					// Assignment 5
		testIndexOf();				// Assignment 6
		testSet();					// Assignment 7
		testRemove();				// Assignment 8
		testAdd();					// Assignment 9
		testIsEmpty();				// Assignment 10
		testContains();				// Assignment 11
		testCopyConstructor();		// Assignment 12
		System.out.println();
	}
	
	public void testAddLast()
	{
		System.out.println("1. Testing addLast(E value)");
		addLastCoordinates(sll);
		System.out.println(sll);
	}
	
	public void testAddFirst()
	{
		System.out.println("\n2. Testing addFirst(E value) [ 5, 5]");
		sll.addFirst(new Coordinate(5, 5));
		System.out.println(sll);
	}

	public void testClear()
	{
		System.out.println("\n3. Testing clear()");
		sll.clear();
		System.out.println("List after clear():");
		System.out.println(sll);
		System.out.println();
	}

	public void testGetHeadTail()
	{
		System.out.println("\n4. Testing getHead() and getTail()");
		addLastCoordinates(sll);
		System.out.println(sll);
		ListNode<Coordinate> node = sll.getHead();
		System.out.println("Node getHead(): " + sll.getHead().getValue());
		System.out.println("Node getTail(): " + sll.getTail().getValue());
		System.out.println();
	}
	
	public void testSize()
	{
		System.out.println("\n5. Testing size()");
		System.out.println(sll);
		System.out.println("size = " + sll.size());
	}
	
	public void testIndexOf()
	{
		System.out.println("\n6. Testing indexOf(E value)");
		System.out.println("sll.indexOf([1, 0]) = " + sll.indexOf(new Coordinate(1, 0)));
		System.out.println("sll.indexOf([2, 1]) = " + sll.indexOf(new Coordinate(2, 1)));
		System.out.println("sll.indexOf([3, 0]) = " + sll.indexOf(new Coordinate(3, 0)));
	}
		
	public void testSet()
	{
		System.out.println("\n7. Testing set(int index, E value)");
		System.out.println("Before: ");
		System.out.println(sll);
		System.out.println("Changing [ 1, 1] to [ 111, 222]");
		Coordinate c1 = new Coordinate(1, 1);
		Coordinate c2 = new Coordinate(111, 222);
		int index = sll.indexOf(c1);
		Coordinate oldCoord = sll.set(index, c2);
		System.out.println("index = " + index + "  oldCoord = " + oldCoord);
		System.out.println("After: ");
		System.out.println(sll);
		System.out.println("Try setting index = 200");
		try
		{
			sll.set(200, new Coordinate(22, 33));
		}
		catch (NoSuchElementException e)
		{	System.err.println("ERROR: NoSuchElementException no index = 200"); }
	}
	
	public void testRemove()
	{
		System.out.println("\n8. Testing remove(int index)");
		System.out.println("Before:");
		System.out.println(sll);
		System.out.println("Remove [111, 222]");
		Coordinate c1 = new Coordinate(111, 222);
		int index = sll.indexOf(c1);
		Coordinate c2 = sll.remove(index);
		System.out.println("After:");
		System.out.println(sll);
		System.out.println("Try removing index = 250");
		try
		{
			sll.remove(250);
		}
		catch (NoSuchElementException e)
		{	System.err.println("ERROR: NoSuchElementException no index = 250"); }
	}
	
	public void testAdd() {
		System.out.println("\n9. Testing add(int index, E obj)");
		System.out.println("Before:");
		System.out.println(sll);
		System.out.println("Adding [1, 1] to index 4");
		sll.add(4, new Coordinate(1, 1));
		System.out.println("After:");
		System.out.println(sll);
		System.out.println("Trying to add to index 20");
		try
		{
			sll.add(20, new Coordinate(66, 66));
		}
		catch (NoSuchElementException e)
		{	System.err.println("ERROR: NoSuchElementException no index = 20"); }
	}
	
	public void testIsEmpty()
	{
		System.out.println("\n10. Testing isEmpty");
		System.out.println("sll: " + sll);
		System.out.println("sll.isEmpty() = " + sll.isEmpty());
		System.out.println("Clearing sll");
		sll.clear();
		System.out.println("sll: " + sll);
		System.out.println("sll.isEmpty() = " + sll.isEmpty());
	}
	
	public void testContains()
	{
		System.out.println("\n11. Testing contains(E value)");
		addLastCoordinates(sll);
		System.out.println(sll);
		Coordinate c1 = new Coordinate(1, 2);
		System.out.println("sll.contains([1, 2]) = " + sll.contains(c1));
		Coordinate c2 = new Coordinate(22, 23);
		System.out.println("sll.contains([22, 23]) = " + sll.contains(c2));
	}
	
	public void testCopyConstructor()
	{
		System.out.println("\n12. Testing Copy Constructor");
		SinglyLinkedList<Coordinate> sll2 = new SinglyLinkedList<Coordinate>(sll);
		System.out.println("Copy list");
		System.out.println("sll2: " + sll2);
		System.out.println("Change index 4 in original sll");
		sll.set(4, new Coordinate(101, 102));
		System.out.println("sll:  " + sll);
		System.out.println("sll2: " + sll2);
	}
		
	/**********************************************************************/
	/******************* Helper methods for testing ***********************/
	/**********************************************************************/
	public void addLastCoordinates(SinglyLinkedList<Coordinate> sll)
	{		
		for (int row = 0; row < NUM_COORDS; row++)
			for (int col = 0; col < NUM_COORDS; col++)
				sll.addLast(new Coordinate(row, col));
	}
	public void addFirstCoordinates(SinglyLinkedList<Coordinate> sll)
	{		
		for (int row = 5; row < 5 + NUM_COORDS; row++)
			for (int col = 5; col < 5 + NUM_COORDS; col++)
				sll.addFirst(new Coordinate(row, col));
	}
}