package runner;
import components.Deadline;
import components.Event;
import components.Task;
import components.Todo;

import java.time.format.DateTimeParseException;

/**
 * Parser to parse the command process the necessary actions.
 */
public class Parser {
    private final Duke duke;

    /**
     * Constructor for Parser.
     * @param duke a Duke chat-bot to work on.
     */
    public Parser(Duke duke) {
        this.duke = duke;
    }


    /**
     * @param info The information given by the user.
     */
    public void handle(String info) {
        if (info.equals("bye")) {
            terminate();
        } else if (info.equals("list")) {
            duke.taskList.showList();
        } else {
            read(info);
        }
    }

    public void read(String info) {
        if (!info.contains(" ")) {
            System.out.println("Not Smart to Understand -_-");
            return;
        }
        try {
            String[] segments = info.split(" ", 2);
            String first = segments[0];
            String arg = segments[1];
            switch (first) {
                case "mark":
                    mark(arg);
                    break;
                case "unmark":
                    unmark(arg);
                    break;
                case "todo":
                    add_todo(info);
                    break;
                case "deadline":
                    add_deadline(arg);
                    break;
                case "event":
                    add_event(arg);
                    break;
                case "delete":
                    delete(arg);
                    break;
                default:
                    System.out.println("OOPS!!! I'm sorry, but I don't know what that means :-(");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("OOPS!!! Arguments not enough.");
        }
    }

    public void terminate() {
        Ui.ending();
        duke.exit = 1;
    }

    public void mark(String s) {
        try {
            int n = Integer.parseInt(s) - 1;
            Ui.markMSG(duke.taskList.get(n));
            duke.taskList.get(n).done = 1;
            duke.store.saveList();
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Index Out");
        }
    }

    public void unmark(String s) {
        try {
            int num = Integer.parseInt(s) - 1;
            Ui.unmarkMSG(duke.taskList.get(num));
            duke.taskList.get(num).done = 0;
            duke.store.saveList();
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Index Out");
        }
    }

    public void delete(String s) {
        try {
            int index = Integer.parseInt(s) - 1;
            Ui.deleteMSG(duke.taskList.get(index), duke.taskList.size()-1);
            duke.taskList.remove(index);
            duke.store.saveList();
        } catch (IndexOutOfBoundsException a) {
            System.out.println("OOPS!!! You can not delete air~");
        } catch (NumberFormatException b) {
            System.out.println("OOPS!!! Number Format Wrong");
        }
    }

    public void add_todo(String info) {
        String action = info.split(" ", 2)[1];
        Task t = new Todo(action);
        duke.taskList.add(t);
        duke.store.saveList();
        Ui.addMSG(t, duke.taskList.size());
    }

    public void add_deadline(String s) {
        try {
            String msg = s.split("/by ", 2)[0];
            String by = s.split("/by ", 2)[1];
            Task d = new Deadline(msg, by);
            duke.taskList.add(d);
            duke.store.saveList();
            Ui.addMSG(d, duke.taskList.size());
        } catch (DateTimeParseException e) {
            System.out.println("Date Unacceptable (YYYY-MM-DD PLZ)");
        }
    }

    public void add_event(String s) {
        String event = s.split("/from", 2)[0];
        String time = s.split("/from", 2)[1];
        String from = time.split("/to")[0];
        String to = time.split("/to")[1];
        Task e = new Event(event, from, to);
        duke.taskList.add(e);
        duke.store.saveList();
        Ui.addMSG(e, duke.taskList.size());
    }
}
