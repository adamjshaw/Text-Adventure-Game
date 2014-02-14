import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public class Main {
    private static JFrame Universe;
    private Location currentLoc;
    private Exit MainExit;
    private boolean hasSword;
    private boolean hasKey;
    private TextField command;
    private TextArea TextOutput;
    private Button okay;
    private Label label;
    public static void main(String[] args) {
        Main a = new Main();
    }
    public Main() {
        Location L1 = new Location ("Entrance", "You are in the entryway of a seemingly abandoned dungeon. ");
        Location L2 = new Location ("Courtyard", "You enter a courtyard where all the plants seem to have died long ago. There is a pond full of dead fish. If I were you, I would not stay in here very long." +"\n" + "To the left, you see a window, looking out onto a steep cliff. The only non-fatal exit is back where you came");
        Location L3 = new Location ("Dim Hallway", "You enter a dimly lit hallway, with doors on the east and south walls");
        Location L4 = new Location ("Knight's room", "You find a fatally wounded knight lying on the ground. With his last ounce of strength he tells you to avenge his death and slay the horrible dragon." + "\n" + "He dies, and his sword is left on the ground. This could be a useful weapon.");
        Location L5 = new Location ("Corpse-filled room", "You enter a room with a barely visible floor, on account of the decaying corpses littered throughout. I would advise caution.");
        Location L6 = new Location ("Dragon's Lair", "You encounter a horrible beast no doubt responsible for the room of corpses." + "\n" + "You'd better leave quick unless you have a weapon of some sort.");
        Location L7 = new Location ("", "");
        L1.addExit(new Exit("WEST", L2));
        L2.addExit(new Exit("EAST", L1));
        L1.addExit(new Exit("NORTH", L3));
        L3.addExit(new Exit("SOUTH", L1));
        L3.addExit(new Exit("EAST", L5));
        L1.addExit(new Exit("EAST", L4));
        L4.addExit(new Exit("WEST", L1));
        L4.addSword();
        Exit e = new Exit("EAST", L7);
        MainExit = e;
        e.lock();
        L4.addExit(e);
        L5.addExit(new Exit("NORTH", L6));
        L5.addExit(new Exit("WEST", L3));
        L6.addExit(new Exit("SOUTH", L5));
        L6.dragon = true;
        currentLoc = L1;
        Universe = new JFrame();
        Panel output = new Panel();
        Panel input = new Panel();
        okay = new Button("OK");
        okay.addActionListener(new okayPressed());
        command = new TextField(20);
        TextOutput = new TextArea(10, 100);
        label = new Label("Commands go here >>>");
        input.add(label);
        input.add(command);
        input.add(okay);
        output.add(TextOutput);
        Universe.getContentPane().add(BorderLayout.SOUTH, input);
        Universe.getContentPane().add(BorderLayout.CENTER, output);
        Universe.setTitle("Dungeon Quest");
        Universe.setSize(800,400);
        Universe.setVisible(true);
        showLocation();
    }
    public void pick_up_key() {
        hasKey = true;
    }
    public void lose_key() {
        hasKey = false;
    }
    public void pick_up_sword() {
        hasSword = true;
    }
    public void drop_sword() {
        hasSword = false;
    }
    public boolean key() {
        return hasKey;
    }
    public boolean sword() {
        return hasSword;
    }
    private void showLocation()
 {
  // Show room title
  TextOutput.appendText( "\n" + currentLoc.getTitle() + "\n" );
  TextOutput.appendText( "\n" );

  // Show room description
  TextOutput.appendText( currentLoc.getDescription() + "\n" );

  // Show available exits
  TextOutput.appendText( "\nPossible Exits : \n" );
                for (int i = 0; i < currentLoc.getExits().size(); ++i) {
   Exit exit = (Exit) currentLoc.getExits().get(i);
   TextOutput.appendText (exit + "\n");
  }

 }
    public boolean act() {
        String a;
        a = command.getText();
        if (a.length() == 0)
            return true;
        a = a.toUpperCase();
        for (int i = 0; i < currentLoc.getExits().size(); ++i) {
            Exit exit = (Exit) currentLoc.getExits().get(i);
            if (a.compareTo(exit.getDirName()) == 0 || (a.compareTo(exit.getDirName().substring(0,1)) == 0)) {
                if (exit.isLocked()) {
                    TextOutput.appendText("\nYou try for six minutes to open the door, but it does not budge. It is most probably locked.\n"+ "Perhaps try fitting a key-shaped object in the lock...\n");
                    command.setText (new String());
                    return true;
                }
                else {
                    if (exit == MainExit) {
                        TextOutput.appendText("\nYou have successfully escaped this cryptic dungeon!!! Congratulations, and thank you for playing!\n");
                        command.setText(new String());
                    }
                    currentLoc = exit.getLeadsTo();
                    showLocation();
                    command.setText (new String());
                    return true;
                }
            }
        }
        if (a.compareTo("USE KEY") == 0) {
            if (!key()) {
                TextOutput.appendText("\nI'm afraid you have no key\n");
                command.setText(new String());
                return true;
            }
            for (int i = 0; i < currentLoc.getExits().size(); ++i) {
                Exit exit = (Exit) currentLoc.getExits().get(i);
                if (exit.isLocked()) {
                    if (exit == MainExit) {
                        TextOutput.appendText("\nYou have successfully escaped this cryptic dungeon!!! Congratulations, and thank you for playing!\n");
                        command.setText(new String());
                        return true;
                    }
                    exit.unlock();
                    currentLoc = exit.getLeadsTo();
                    showLocation();
                    command.setText(new String());
                    return true;
                }
            }
            TextOutput.appendText("\nThere are no doors that the key will work for!\n");
            command.setText(new String());
            return true;
        }
        if (a.compareTo("TAKE KEY") == 0) {
            if (currentLoc.hasKey()) {
                pick_up_key();
                currentLoc.removeKey();
                currentLoc.setRoomDescription("There is nothing left of interest in this room");
                TextOutput.appendText("\nYou have found a key!!\n");
                command.setText(new String());
                return true;
            }
            TextOutput.appendText("\nThere are no keys in this room!\n");
            command.setText(new String());
            return true;
        }
        if (a.compareTo("USE SWORD") == 0) {
            if (sword()) {
            if (currentLoc.dragon) {
                TextOutput.appendText("\nYou have slain the dragon!!");
                currentLoc.dragon = false;
                currentLoc.setRoomDescription("The majestic dragon that took the lives of countless knights lies slain on the ground." + "\n" + "With the dragon no longer blocking your view, you can see a key on the ground");
                currentLoc.addKey();
                showLocation();
                command.setText(new String());
                return true;
            }
            TextOutput.appendText("\nI think you'll want to save that sword for any possible dragon encounters");
            command.setText(new String());
            return true;
            }
            TextOutput.appendText("\nI'm afraid you have no sword!\n");
            command.setText(new String());
            return true;
        }
        if (a.compareTo("TAKE SWORD") == 0) {
            if (currentLoc.hasSword()) {
                TextOutput.appendText("\nYou have acquired a sword!!\n");
                currentLoc.removeSword();
                currentLoc.setRoomDescription("You are in a small, inauspicious-looking room with a dead knight and one unimportant-looking exit to the east in it");
                pick_up_sword();
                command.setText(new String());
                return true;
            }
            TextOutput.appendText("\nWhat sword?\n");
            command.setText(new String());
            return true;
        }
        if (a.compareTo("HELP") == 0) {
            showLocation();
            TextOutput.appendText("\nType a direction, or the first letter of a direction. Also try typing take, or use, plus an object, to take or use certain objects\n");
            command.setText(new String());
            return true;
        }
        TextOutput.appendText("\nI'm afraid I do not understand\n");
        command.setText(new String());
        return false;
    }
    class okayPressed implements ActionListener {
        public void actionPerformed (ActionEvent event) {
            act();
        }
    }

}
class Location {
    private String roomTitle;
    private int roomNum;
    private String roomDescription;
    private ArrayList <Exit> exits;
    private boolean key;
    private boolean sword;
    public boolean dragon;
    public Location (String title, String description) {
        roomTitle = title;
        roomDescription = description;
        exits = new ArrayList();
        key = false;
        sword = false;
    }
    public String getTitle () {
        return roomTitle;
    }
    public String getDescription () {
        return roomDescription;
    }
    public void setRoomTitle (String title) {
        roomTitle = title;
    }
    public void setRoomDescription (String des) {
        roomDescription = des;
    }
    public void addExit (Exit e) {
        exits.add(e);
    }
    public void removeExit (Exit e) {
        if (exits.contains(e))
            exits.remove(e);
    }
    public boolean hasKey() {
        return key;
    }
    public boolean hasSword() {
        return sword;
    }
    public void addKey() {
        key = true;
    }
    public void addSword() {
        sword = true;
    }
    public void removeKey() {
        key = false;
    }
    public void removeSword() {
        sword = false;
    }
    public ArrayList getExits () {
        return (ArrayList) exits.clone();
    }
}
class Exit {
    private Location leads_to;
    private int DirNum;
    private String DirName;
    private boolean locked;
    public Exit (String direction, Location loc) {
        leads_to = loc;
        DirName = direction;
        locked = false;
    }
    public void setDirName (String dir) {
        DirName = dir;
    }
    public String getDirName() {
        return DirName;
    }
    public void setLeadsTo (Location loc) {
        leads_to = loc;
    }
    public Location getLeadsTo() {
        return leads_to;
    }
    public String toString() {
        return DirName;
    }
    public boolean isLocked() {
        return locked;
    }
    public void lock() {
        locked = true;
    }
    public void unlock() {
        locked = false;
    }
}

