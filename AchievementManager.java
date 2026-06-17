import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class AchievementManager
{
    private static final String FILE = "achievements.txt";
    private static Set<String> unlocked = null;

    private static Set<String> load()
    {
        if(unlocked != null) return unlocked;
        unlocked = new HashSet<>();
        File f = new File(FILE);
        if(!f.exists()) return unlocked;
        try(BufferedReader br = new BufferedReader(new FileReader(f)))
        {
            String line;
            while((line = br.readLine())!= null)
            {
                line = line.trim();
                if(!line.isEmpty())
                {
                    unlocked.add(line);
                }
                line = br.readLine();
            }
            br.close();
        }
        catch(IOException e)
        {
            System.out.println("couldn't read achievements file");
        }
        return unlocked;
    }

    private static HashSet<String> getUnlockedSet()
    {
        if(unlocked == null) load();
        return (HashSet<String>) unlocked;
    }

    private static void unlock(String name)
    {
        HashSet<String> set = getUnlockedSet();
        if(set.contains(name))
        {
            return;
        }
        set.add(name);
        System.out.println("Achievement Unlocked: "+name);

        try{
            PrintWriter pw = new PrintWriter(new FileWriter(FILE,true));
            pw.println(name);
            pw.close();
        }
        catch(IOException e )
        {
            System.out.println("couldn't save achiveiemnts: "+ name);
        }

    }

    public static boolean check(String event)
    {
        HashSet<String> set = getUnlockedSet();
        int before = set.size();

        if(event.startsWith("level complete:"))
        {
            String numStr = event.replace("level_complete:", "");
            int lvl = Integer.parseInt(numStr);

            unlock("First Blood");

            if(lvl == 5)
            {
                unlock("GrandMaster");
            }
        }
        else if(event.startsWith("combo:"))
        {
            String comboName = event.replace("combo:", "");
            if(comboName.equals("Death Blossom"))
            {
                unlock("Death Blossom");
            }

            unlock("did:" + comboName);

            boolean allDone = true;
            String[] allCombos = {"Shadow Kick","Hurricane Kick","Dragon Sweep","Counter Burst","Death Blossom"};

            for(int i =0;i<allCombos.length;i++)
            {
                if(!set.contains("did:"+allCombos[i]))
                {
                    allDone = false;
                }
            }

            if(allDone == true)
            {
                unlock("Combo Master");
            }
        }
        else if(event.equals("untouched_win"))
        {
            unlock("Untouchable");
        }

        return set.size()>before;
    }

    public static String getSummary()
    {
        HashSet<String> set = getUnlockedSet();
        String result = "";

        for(String s: set)
        {
            if(!s.startsWith("did:"))
            {
                result = result + " *"+s+"\n";
            }
        }
        if (result.equals(""))
        {
            return " (none yet)";
        }
        return result;
    }
}

