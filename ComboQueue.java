// ComboQueue.java
// A queue of recent moves built using the custom SinglyLinkedList and ListNode classes.

class Move implements Comparable<Move>
{
    private String name;
    private long timestamp;

    public Move(String nameIn, long timestampIn)
    {
        name = nameIn;
        timestamp = timestampIn;
    }

    public String getName()
    {
        return name;
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public int compareTo(Move other)
    {
        if (this.timestamp < other.timestamp)
        {
            return -1;
        }
        else if (this.timestamp > other.timestamp)
        {
            return 1;
        }
        return 0;
    }

    public String toString()
    {
        return name;
    }
}

public class ComboQueue
{
    private SinglyLinkedList<Move> list;
    private static final long TIME_LIMIT = 1500;

    public ComboQueue()
    {
        list = new SinglyLinkedList<Move>();
    }

    public void addMove(String moveName)
    {
        long currentTime = System.currentTimeMillis();
        Move newMove = new Move(moveName, currentTime);
        list.addLast(newMove);
        cleanUp();
    }

    public void cleanUp()
    {
        long currentTime = System.currentTimeMillis();
        while (list.getHead() != null && (currentTime - list.getHead().getValue().getTimestamp() > TIME_LIMIT))
        {
            list.remove(0);
        }
    }

    public String checkCombo()
    {
        cleanUp();
        if(list.size()<3)
        {
            return null;
        }

        String seq = "";
        ListNode<Move> current = list.getHead();
        while(current!= null)
        {
            seq = seq+current.getValue().getName()+",";
            current = current.getNext();
        }

        if(list.size()>=4 && seq.contains("Uppercut,Kick,Punch,Kick"))
        {
            clear();
            return "Death Blossom";
        }
        if(seq.contains("Block,Punch,Roundhouse"))
        {
            clear();
            return "Counter Burst";
        }
        if(seq.contains("Punch,Uppercut,Roundhouse"))
        {
            clear();
            return "Hurricane Kick";
        }
        if(seq.contains("Kick,Kick,Uppercut"))
        {
            clear();
            return "Dragon Sweep";
        }
        if(seq.contains("Punch,Punch,Kick"))
        {
            clear();
            return "Shadow Kick";
        }
        return null;

    }   

    public void clear()
    {
        list.clear();
    }
}
