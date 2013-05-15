/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.uqac.info.util;

/**
 * Ecouteurs à prévenir quand un Monitor change de verdict.
 * @author armand
 */
public interface OnNotifyListener {
    public void onNotify();
    public void onVerdictChange();
}
