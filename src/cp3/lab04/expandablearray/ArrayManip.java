/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package cp3.lab04.expandablearray;

/**
 *
 * @author hmoses
 */
public class ArrayManip extends Thread {

    ExpandableArray array;

    public ArrayManip(ExpandableArray array) {
        this.array = array;
        
    }
    public void run(){
        for (int i = 0; i < 200; i++){
            try {array.add(i);}
            catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Array is full, cannot add " + i);
            }
        }
    }

}
