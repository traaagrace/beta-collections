package blockqueue;

import java.util.concurrent.LinkedBlockingQueue;

public class LinkedBlockingQueueDemo {
    private static volatile LinkedBlockingQueue<String> linkedBlockingQueue;

    public LinkedBlockingQueue getLinkedBlockingQueue(){
        if(linkedBlockingQueue == null){
            synchronized (LinkedBlockingQueueDemo.class){
                if(linkedBlockingQueue == null){
                    linkedBlockingQueue = new LinkedBlockingQueue<>();
                }
            }
        }
        return linkedBlockingQueue;
    }

    private void take(){
        try {
            String take = linkedBlockingQueue.take();
        } catch (InterruptedException e) {

        }

    }

    private void put(){

    }
    public static void main(String[] args) throws InterruptedException {
    }
}
