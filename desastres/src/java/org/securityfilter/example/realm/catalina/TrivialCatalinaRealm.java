/*
 * $Header: /cvsroot/securityfilter/securityfilter/src/example/org/securityfilter/example/realm/catalina/TrivialCatalinaRealm.java,v 1.3 2004/01/26 09:31:34 maxcooper Exp $
 * $Revision: 1.3 $
 * $Date: 2004/01/26 09:31:34 $
 *
 * ====================================================================
 * The SecurityFilter Software License, Version 1.1
 *
 * (this license is derived and fully compatible with the Apache Software
 * License - see http://www.apache.org/LICENSE.txt)
 *
 * Copyright (c) 2002 SecurityFilter.org. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by
 *        SecurityFilter.org (http://www.securityfilter.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The name "SecurityFilter" must not be used to endorse or promote
 *    products derived from this software without prior written permission.
 *    For written permission, please contact license@securityfilter.org .
 *
 * 5. Products derived from this software may not be called "SecurityFilter",
 *    nor may "SecurityFilter" appear in their name, without prior written
 *    permission of SecurityFilter.org.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE SECURITY FILTER PROJECT OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 */

package org.securityfilter.example.realm.catalina;

import org.apache.catalina.realm.*;
import org.securityfilter.example.Constants;

import java.security.Principal;
import java.util.ArrayList;

/**
 * TrivialCatalinaRealm - Trivial Catalina Realm implementation to demonstrate
 * org.securityfilter.realm.catalina.CatalinaRealmAdapter adapter class.
 *
 * @author Max Cooper (max@maxcooper.com)
 * @version $Revision: 1.3 $ $Date: 2004/01/26 09:31:34 $
 */
public class TrivialCatalinaRealm extends RealmBase {

   private String exampleProperty;

   /**
    * Return a short name for this Realm implementation.
    */
   protected String getName() {
      return null;
   }

   /**
    * Return the password associated with the given principal's user name.
    */
   protected String getPassword(String s) {
      return (Constants.VALID_USERNAME.equals(s) ? Constants.VALID_PASSWORD : null);
   }

   /**
    * Return the Principal associated with the given user name.
    */
   protected Principal getPrincipal(String s) {
      if (Constants.VALID_USERNAME.equals(s)) {
         ArrayList roleList = new ArrayList();
         roleList.add(Constants.VALID_ROLE);
         return new GenericPrincipal(this, Constants.VALID_USERNAME, Constants.VALID_PASSWORD, roleList);
      } else {
         return null;
      }
   }

   /**
    * Setter for exampleProperty to deomonstrate setting realm properties from config file.
    *
    * This has no effect other than printing a message when the property is set.
    *
    * @param value example property value
    */
   public void setExampleProperty(String value) {
      exampleProperty = value;
      System.out.println(this.getClass().getName() + ": exampleProperty set to \'" + value + "\'");
   }

   /**
    * Getter for exampleProperty.
    *
    * @return the value of exampleProperty
    */
   public String getExampleProperty() {
      return exampleProperty;
   }
}

// ----------------------------------------------------------------------------
// EOF