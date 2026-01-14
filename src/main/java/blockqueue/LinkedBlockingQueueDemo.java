package blockqueue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class LinkedBlockingQueueDemo {
    private static volatile LinkedBlockingQueue<String> linkedBlockingQueue;

    public static LinkedBlockingQueue<String> getLinkedBlockingQueue(){
        if(linkedBlockingQueue == null){
            synchronized (LinkedBlockingQueueDemo.class){
                if(linkedBlockingQueue == null){
                    linkedBlockingQueue = new LinkedBlockingQueue<>();
                }
            }
        }
        return linkedBlockingQueue;
    }

    private static void take(){
        LinkedBlockingQueue<String> linkedBlockingQueue1 = getLinkedBlockingQueue();
        linkedBlockingQueue1.remove();
    }

    private static void put(){
        LinkedBlockingQueue<String> linkedBlockingQueue1 = getLinkedBlockingQueue();
        linkedBlockingQueue1.offer("anything");
    }
    public static void main(String[] args) throws InterruptedException {
        put();take();
        ArrayBlockingQueue<String> arrayBlockingQueue = new ArrayBlockingQueue<>(10);
        arrayBlockingQueue.put("anything");
        arrayBlockingQueue.take();
    }
}
