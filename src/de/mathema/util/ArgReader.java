/*******************************************************************************
 * Copyright (c) 2001-2007 The PriDE team and MATHEMA Software GmbH
 * All rights reserved. This toolkit and the accompanying materials 
 * are made available under the terms of the GNU Lesser General Public
 * License (LGPL) which accompanies this distribution, and is available
 * at http://pride.sourceforge.net/LGPL.html
 * 
 * Contributors:
 *     Jan Lessner, MATHEMA Software GmbH - initial API and implementation
 *******************************************************************************/
package de.mathema.util;

import java.util.Hashtable;

public class ArgReader {

  public static final char ARGEND = 0;

  private Hashtable hashPattern;
  private String[] args;
  private String argpattern;
  private String argValue = null;
  private int intI = 0;
  private int intL = 0;

  public ArgReader(String[] args, String argpattern) {
    this.args = args;
    this.argpattern = argpattern;
    this.hashPattern = new Hashtable();

    for (int i = 0; i < argpattern.length()-1; i++) {
      if(argpattern.charAt(i) != ':') {
        if(argpattern.charAt(i+1) == ':') {
          hashPattern.put(String.valueOf(argpattern.charAt(i)), ":");
        }
        else {
          hashPattern.put(String.valueOf(argpattern.charAt(i))," ");
        }
      }
    }
  }

  public char getArg() throws ArgumentException {
    try{

      char result = ARGEND;
      boolean flag = false;

      if (intI < args.length) {
        if(args[intI].startsWith("-")) {
          if(hashPattern.get(String.valueOf(args[intI].charAt(1))) != null) {
            if(hashPattern.get(String.valueOf(args[intI].charAt(1))) == ":") {
              if(args[intI].length() > 2) {
                argValue = args[intI].substring(2);
                args[intI] = "-" + args[intI].charAt(1);
                flag = false;
              }
              else {
                argValue = args[intI + 1];
                flag = true;
              }
            }
            else {
              argValue = null;
            }
            result = args[intI].charAt(1);

            if(args[intI].length() > 2) {
              args[intI] = "-" + args[intI].substring(2);
            }
            else {
              if(flag == true) {
                intI++;
              }
              intI++;
            }
          }
          else {
            throw new ArgumentException("Invalid argument: " + args[intI].charAt(1));
          }
        }
        else {
          result = ARGEND;
        }
      }
      else {
        result = ARGEND;
      }
      return result;
    }
    catch (Exception ex) {
      throw new ArgumentException("Parameter is missing");
    }
  }

  public String getArgValue() {
    return argValue;
  }

   public String[] getPendingArgs() {

      String[] pendingargs = new String[args.length - intI];

      int l = 0;

      for(int i = intI; i < args.length; i++) {
        pendingargs[l++] = args[i];
      }
      return pendingargs;
   }
}
