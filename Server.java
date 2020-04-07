import java.util.Scanner;

public class Server {
    public static void main(String[] args) {
        Sender sender = new Sender();
        Receiver receiver = new Receiver();
        Scanner sc = new Scanner(System.in);
        while(true){
        System.out.println("Enter receiver discard rate (0 <= x < 1). Default 0");
        try{
        float percent = Float.parseFloat(sc.nextLine());
        if (percent>=0 && percent < 1){
            receiver.setPercent(percent);
            break;
        }else{
            System.out.println("use correct discard rate.");
        }
    }catch(NumberFormatException e ){
        break;
    }
    }
    System.out.println("Begin communication:\n\n");
    sender.start();
    receiver.start();
    }
}