//import java.util.ArrayList;
//
//public class MinHeap<PCB> {
//    // Member variables of this class
//    private ArrayList<PCB> Heap;
//    private int size;
//
//    // Initializing front as static with unity
//    private static final int FRONT = 1;
//
//    // Constructor of this class
//    public MinHeap(int maxsize)
//    {
//        this.size = 0;
//
//        Heap = new ArrayList<>();
//    }
//
//    // Method 4
//    // Returning true if the passed
//    // node is a leaf node
//    private boolean isLeaf(int pos) { return pos > (size / 2); }
//
//    // Method 5
//    // To swap two nodes of the heap
//    private void swap(int fpos, int spos)
//    {
//        PCB tmp;
//        tmp = Heap.get(fpos);
//
//        Heap.set(fpos, Heap.get(spos));
//        Heap.set(spos, tmp);
//    }
//
//    // Method 6
//    // To heapify the node at pos
//    private void minHeapify(int pos)
//    {
//        if(!isLeaf(pos)){
//            int swapPos= pos;
//            // swap with the minimum of the two children
//            // to check if right child exists. Otherwise default value will be '0'
//            // and that will be swapped with parent node.
//            if(rightChild(pos)<=size)
//                swapPos = Heap[leftChild(pos)]<Heap[rightChild(pos)]?leftChild(pos):rightChild(pos);
//            else
//                swapPos= leftChild(pos);
//
//            if(Heap[pos]>Heap[leftChild(pos)] || Heap[pos]> Heap[rightChild(pos)]){
//                swap(pos,swapPos);
//                minHeapify(swapPos);
//            }
//
//        }
//    }
//
//    // Method 7
//    // To insert a node into the heap
//    public void insert(PCB element)
//    {
//        Heap.set(++size, element);
//        int current = size;
//
//        while (Heap.get(current) < Heap[parent(current)]) {
//            swap(current, parent(current));
//            current = parent(current);
//        }
//    }
//
//    // Method 8
//    // To print the contents of the heap
//    public void print()
//    {
//        for (int i = 1; i <= size / 2; i++) {
//
//            // Printing the parent and both childrens
//            System.out.print(
//                    " PARENT : " + Heap[i]
//                            + " LEFT CHILD : " + Heap[2 * i]
//                            + " RIGHT CHILD :" + Heap[2 * i + 1]);
//
//            // By here new line is required
//            System.out.println();
//        }
//    }
//
//    // Method 9
//    // To remove and return the minimum
//    // element from the heap
//    public int remove()
//    {
//
//        int popped = Heap[FRONT];
//        Heap[FRONT] = Heap[size--];
//        minHeapify(FRONT);
//
//        return popped;
//    }
//}
