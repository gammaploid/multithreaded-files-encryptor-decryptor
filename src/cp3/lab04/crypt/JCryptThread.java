/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package cp3.lab04.crypt;

/**
 *
 * @author hmoses
 */
public class JCryptThread extends Thread {

    private final JCryptUtil.Options opts;
    private final int index;

    public JCryptThread(JCryptUtil.Options opts, int index) {
        this.opts = opts;
        this.index = index;
    }

    @Override
    public void run() {
        try {
            JCrypt.process(opts, index);
        } catch (JCryptUtil.Problem e) {
            System.err.println("ERROR: " + e.getMessage());
            System.exit(2);
        }
    }
}
