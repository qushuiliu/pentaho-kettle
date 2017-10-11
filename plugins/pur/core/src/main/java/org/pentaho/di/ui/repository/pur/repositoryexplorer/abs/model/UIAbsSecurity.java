/*!
 * Copyright 2010 - 2017 Hitachi Vantara.  All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.pentaho.di.ui.repository.pur.repositoryexplorer.abs.model;

import java.util.List;

import org.pentaho.di.repository.RepositorySecurityManager;
import org.pentaho.di.ui.repository.pur.repositoryexplorer.IUIRole;
import org.pentaho.di.ui.repository.pur.repositoryexplorer.abs.IUIAbsRole;
import org.pentaho.di.ui.repository.pur.repositoryexplorer.model.UIEESecurity;
import org.pentaho.di.ui.repository.pur.services.IAbsSecurityManager;

public class UIAbsSecurity extends UIEESecurity implements java.io.Serializable {

  private static final long serialVersionUID = -8131064658827613758L; /* EESOURCE: UPDATE SERIALVERUID */

  public UIAbsSecurity() {
    super();
  }

  public UIAbsSecurity( RepositorySecurityManager rsm ) throws Exception {
    super( rsm );
    for ( IUIRole systemRole : systemRoleList ) {
      if ( rsm instanceof IAbsSecurityManager ) {
        IAbsSecurityManager asm = (IAbsSecurityManager) rsm;
        List<String> logicalRoles = asm.getLogicalRoles( systemRole.getName() );
        if ( systemRole instanceof IUIAbsRole ) {
          ( (IUIAbsRole) systemRole ).setLogicalRoles( logicalRoles );
        } else {
          throw new IllegalStateException();
        }
      } else {
        throw new IllegalStateException();
      }
    }
  }

  public void addLogicalRole( String logicalRole ) {
    IUIRole role = getSelectedRole();
    if ( role != null ) {
      if ( role instanceof IUIAbsRole ) {
        ( (IUIAbsRole) role ).addLogicalRole( logicalRole );
      } else {
        throw new IllegalStateException();
      }
    } else {
      role = getSelectedSystemRole();
      if ( role instanceof IUIAbsRole ) {
        ( (IUIAbsRole) role ).addLogicalRole( logicalRole );
      } else {
        throw new IllegalStateException();
      }
    }
  }

  public void removeLogicalRole( String logicalRole ) {
    IUIRole role = getSelectedRole();
    if ( role != null ) {
      if ( role instanceof IUIAbsRole ) {
        ( (IUIAbsRole) role ).removeLogicalRole( logicalRole );
      } else {
        throw new IllegalStateException();
      }
    } else {
      role = getSelectedSystemRole();
      if ( role instanceof IUIAbsRole ) {
        ( (IUIAbsRole) role ).removeLogicalRole( logicalRole );
      } else {
        throw new IllegalStateException();
      }
    }
  }
}
