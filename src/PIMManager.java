import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.regex.Pattern;

// hello cool go
/**
 * 感觉switch部分太庞大了应该改成方法调用比较好
 * 需不需要为每个子类都加一个专门的list?
 *
 */

public class PIMManager {

    //仅仅把dat改成了pim后缀
    static String dataFilePath = "PIMDatabase.pim";

    static File dataFile;

    static LinkedList<PIMEntity> itemList;

    static Scanner sc;

    public PIMManager() {}

    private static void saveData() {
        if (dataFile.canWrite()) {
            try {
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dataFile));

                try {
                    oos.writeObject(itemList);
                    oos.flush();
                } catch (Throwable var4) {
                    try {
                        oos.close();
                    } catch (Throwable var3) {
                        var4.addSuppressed(var3);
                    }

                    throw var4;
                }

                oos.close();
            } catch (FileNotFoundException var5) {
                var5.printStackTrace();
            } catch (IOException var6) {
                var6.printStackTrace();
            }
        }

        System.out.println("--------⭐Successfully saved⭐---------");
        System.out.println("----------⭐Total " + itemList.size() + " records⭐-----------");
    }

    private static void loadData() {
        if (dataFile.canRead() && dataFile.length() > 0L) {
            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dataFile));

                try {
                    itemList = (LinkedList)ois.readObject();
                } catch (Throwable var4) {
                    try {
                        ois.close();
                    } catch (Throwable var3) {
                        var4.addSuppressed(var3);
                    }
                    throw var4;
                }
                ois.close();
            } catch (Exception var5) {
                var5.printStackTrace();
            }
        }

        System.out.println("--------⭐Successfully loaded⭐--------");
    }

    static void createTodo(){
        //todo 的 date意思是deadline
        String date;
        String text;

        PIMTodo todo = new PIMTodo();

        // 定义日期时间格式化器
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 正则表达式模式
        String pattern = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$";
        boolean isFormatValid;

        do {
            //提示用户输入startTime
            System.out.println("-------------Enter Deadline for todo( format：yyyy-MM-dd HH:mm:ss ) ：");
            date = sc.nextLine();
            isFormatValid = Pattern.matches(pattern, date);

            if (!isFormatValid) {
                System.out.println("------------❗Invalid format❗------------");
            } else {
                isFormatValid = true;
                //break label;  在日期格式正确时跳出循环
            }
        }while (isFormatValid == false);

        try {
            // 解析deadline为LocalDateTime对象
            LocalDateTime deadline = LocalDateTime.parse(date, formatter);

            // 创建事件并进行后续操作
            todo.setDeadline(deadline);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        //提示输入描述, 将描述添加到todo对象中
        System.out.println("-------------Enter todo description:");
        text = sc.nextLine();
        todo.setDescription(text);

        //添加到list
        itemList.add(todo);

        //添加完之后输出反馈
        System.out.println("--------⭐Successfully added⭐---------"+ "\n" + todo);
    }

    static void createEvent(){
        String date;
        String alarmTimeInput;
        String text;
        PIMEvent event = new PIMEvent();

        // 正则表达式模式
        String pattern = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$";
        boolean isFormatValid;

        //正则验证start time格式
        do {
            //提示用户输入startTime
            System.out.println("-------------Enter Start time for event( format：yyyy-MM-dd HH:mm:ss ) ：");
            date = sc.nextLine();
            isFormatValid = Pattern.matches(pattern, date);

            if (!isFormatValid) {
                System.out.println("Invalid format!");
                continue;
            } else {
                isFormatValid = true;
                //break label;  在日期格式正确时跳出循环
            }

        }while (isFormatValid == false);

        //正则验证alarm time格式, 以及是否早于start time
        do {
            // 提示用户输入alarmTime
            System.out.println("-------------Enter Alarm time for event( format：yyyy-MM-dd HH:mm:ss ) ：");
            alarmTimeInput = sc.nextLine();
            isFormatValid = Pattern.matches(pattern, alarmTimeInput);
            if (!isFormatValid) {
                System.out.println("------------❗Invalid format❗------------");
                continue ;
            } else {
                isFormatValid = true;
                // 在日期格式正确时跳出循环
            }
            // 定义日期时间格式化器
            DateTimeFormatter formatter2 =  DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            try {
                // 解析startTime和alarmTime为LocalDateTime对象
                LocalDateTime startTime = LocalDateTime.parse(date, formatter2);
                LocalDateTime alarmTime = LocalDateTime.parse(alarmTimeInput, formatter2);

                // 验证alarmTime是否晚于startTime
                if (!alarmTime.isBefore(startTime)) {
                    throw new IllegalArgumentException(
                            "-------Invalid alarmTime: --------------" + "\n" +
                            "Reminders must be no later than " + date +
                            "----------------------------------------"
                    );
                }
                // 创建事件并进行后续操作
                event.setStart_time(startTime);
                event.setAlarm_time(alarmTime);

            } catch (Exception e) {
                //其实这个exception就包括了任何exception
                System.out.println(e.getMessage());

                //如果提醒时间晚于开始时间, 要回到do处
                isFormatValid = false;
            }
        }while (isFormatValid == false);

        //输入event描述
        System.out.println("-------------Enter event description:");
        text = sc.nextLine();
        event.setDescription(text);

        //添加到list
        itemList.add(event);

        //输出反馈
        System.out.println("--------⭐Successfully added⭐---------"+ "\n" + event);
    }

    static void createNote(){
        String text;

        //输入note内容
        PIMNote note = new PIMNote();
        System.out.println("-------------Enter note text:");
        text = sc.nextLine();
        note.setNoteText(text);

        //添加到list
        itemList.add(note);

        //输出反馈
        System.out.println("--------⭐Successfully added⭐---------" + "\n" + note);

    }

    static void createContact(){
        PIMContact contact = new PIMContact();
        //输入firstname
        System.out.println("-------------Enter firstname :");
        String firstName = sc.nextLine();
        contact.setFirstName(firstName);

        //输入lastname
        System.out.println("-------------Enter lastname :");
        String lastName = sc.nextLine();
        contact.setLastName(lastName);

        //输入地址
        System.out.println("-------------Enter address :");
        String address = sc.nextLine();
        contact.setAddress(address);

        //输入numbers
        System.out.println("-------------Enter mobile numbers :");
        String m = sc.nextLine();
        contact.setPhoneNum(m);

        //把contact添加到itemlist
        itemList.add(contact);

        //输出反馈
        System.out.println("--------⭐Successfully added⭐---------"+ "\n" + contact);
    }

    static void createData(){
        //打印create菜单
        printCreateMenu();

        switch (sc.nextLine()) {
            case "t":
                createTodo();
                saveData();
                break;

            case "n":
                createNote();
                saveData();
                break;

            case "e"://把appoint改成event
                createEvent();
                saveData();
                break;

            case "c":
                createContact();
                saveData();
                break;

            case "b"://返回主菜单
                break ;

            default:
                System.out.println("!!!!!!!!! the item type is not exist !!!!!!!!");
                break;
        }
    }

    /**
     * 删除特定记录:
     * 1.用户键盘输入想要删的index 空格分隔
     * 2.接收并解析为int数组
     * 3.用iterator正序遍历list, remove即可
     * 4.输出反馈, 还剩几个
     */
    private static void deleteData() {
        //判断是否可以删除
        if(itemList.size() >= 1){
            //提示用户选择要删除的记录
            System.out.println("-------------Enter the item number(starts from number 1) to delete " + "\n" +
                    "(separated by spaces): e.g. 2 4 1 5");
            System.out.println("-------------❗❗❗❗❗❗❗❗if you want to delete the 1st record, please enter number 1 ");
            String input = sc.nextLine();

            System.out.println("-------------The items to delete: " + input);
            String[] indexStrings = input.split(" ");
            int[] indices = new int[indexStrings.length];

            //解析用户输入的索引
            for (int i = 0; i < indexStrings.length; i++) {
                //System.out.println("字符串转int: " + Integer.parseInt(indexStrings[i]));// 输入3, 输出3
                indices[i] = Integer.parseInt(indexStrings[i]) - 1;
                //System.out.println("-------------The items to delete: " + indices[i]);//输入3, 输出0, 闹鬼了
            }

            //把待删除index按升序排列
            Arrays.sort(indices);

            //删除记录
            //Iterator<PIMEntity> iterator = itemList.iterator();

            //遍历LinkedList，Iterator删除记录
            //应该是先判断当前的index是否合法, 合法则进入iterator, 非法则继续下一个index
            //但这样从前往后删除, 每次list的长度都变化, 所以要从后往前删除
            for (int i = indices.length - 1; i >= 0; i--){
                int index = indices[i];
                System.out.println("-------------Deleting Item " + (index+1) );
                //判断index合法性, 不使用iterator了, 实在是不好用
                if( index >= 0 && index < itemList.size() ){
                    //index合法:
                    itemList.remove(index);
                    System.out.println("Item " + (index + 1) + " deleted" + "\n");
                }else {
                    //继续循环下一个index,输出删除失败
                    System.out.println("-------------❗Invalid Item Number: " + (index + 1) + "; Failed to delete❗" + "\n");
                }
            }

            //删除之后要save
            System.out.println("-------------Do you want to save the deletion?  Enter y or n");

            //saveData();
            //输出反馈, 删除过程结束
            System.out.println("-------------⭐End of deletion⭐" + "\n" +
                    "             ⭐" + itemList.size() + " item(s) left.");
        }else {
            System.out.println("-------------❗There's no record to delete❗");
        }
    }

    static void printList(){
        System.out.println("There are " + itemList.size() + " items.");
        int i = 0;

        while(true) { //这一段打包成一个printList()
            if (i >= itemList.size()) {
                break;//跳出while循环, 也就是该printlist()执行完毕, 继续回到enter a command
            }
            System.out.println(
                    "--------------⭐Item " + (i + 1) + "⭐---------------" + "\n"+
                            itemList.get(i));
            ++i;
        }
    }

    /**
     * 打印主菜单
     */
    /**
     * 11-16: 增加删除功能
     */

    static void printMainMenu(){
        System.out.println("----------------------------------------");
        System.out.println("-          Enter a command             -");
        System.out.println("----------------------------------------");
        System.out.println(
                        "-            l -> list                 -" +"\n"+
                        "-            c -> create               -" +"\n"+
                        "-            s -> save                 -" +"\n"+
                        "-            e -> exit                 -" +"\n"+
                        "-            d -> delete               -" +"\n"+
                        "----------------------------------------");
    }

    /**
     * 打印create各种事项的菜单
     */
    //如果不想创建, 增加一个返回主菜单
    static void printCreateMenu(){
        System.out.println("----------------------------------------");
        System.out.println("-      Enter an item type(number)      -");
        System.out.println("----------------------------------------");
        System.out.println(
                        "-            t -> todo                 -" +"\n"+
                        "-            n -> note" +"\n"+
                        "-            c -> contact" +"\n"+
                        "-            e -> event" +"\n"+
                        "-            b -> back to Main Menu" +"\n"+
                        "----------------------------------------");
    }
    public static void main(String[] args) throws IOException {
        if (!dataFile.exists()) {
            dataFile.createNewFile();
        } else {
            loadData();
        }

        //感觉这个开始语句有点不显眼, 看能不能整点花样, 加点那种程序员图什么的
        System.out.println("----------------------------------------");
        System.out.println("*          Welcome to PIM:)            *");
        System.out.println("----------------------------------------");

        label74:
        do {
            //打印这个主菜单打包成一个printMainMenu()
            printMainMenu();
            switch (sc.nextLine()) {
                case "l":
                    printList();
                    continue ;
                case "c":
                    createData();
                    //自动保存
                    continue ;
                case "s":
                    saveData();
                    continue ;
                //这里把 break 改成 break label74, 就不会跳到 108行 的输入命令
                case "e":
                    sc.close();
                    break label74;
                case "d" :
                    deleteData();//删除之后不会自动保存, 便于测试!
                    continue ;

                default:
                    System.out.println("-------------❗the command is not exist❗");
            }
        } while(true);//感觉这个条件直接改成while( 1 )死循环也可以啊
    }



    /**
     * 一个静态代码块，它在类加载时执行，并且只会执行一次。静态代码块的主要目的是在类加载时进行一些初始化操作。
     *
     * 根据代码片段提供的信息，可以推断出以下几点：
     *
     * dataFilePath 是一个字符串变量，表示数据文件的路径。
     * dataFile 是一个 File 对象，用于表示数据文件。
     * itemList 是一个 LinkedList 对象，用于存储数据项。
     */
    static {
        dataFile = new File(dataFilePath);
        itemList = new LinkedList();
        sc = new Scanner(System.in);

    }

    //hello
}
