/*
 * Copyright 2001-2006 The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

function other_choice(dropDown) {
    opt = dropDown.options[dropDown.selectedIndex];
    ret = false;
    if (opt.value == "_OTHER_") ret = true;
    return ret;
}

function activate(field) {
  field.disabled=false;
  if(document.styleSheets)field.style.visibility  = 'visible';
  field.focus(); 
}

function process_choice(selection,textfield) {
  b = other_choice(selection);
  if(b) {
    activate(textfield); }
  else {
    textfield.disabled = true;    
    if(document.styleSheets)textfield.style.visibility  = 'hidden';
    textfield.value = ''; 
  }
}

function check_choice(dropDown) {
  if(!other_choice(dropDown)) {
    dropDown.blur();
    alert('Please check your menu selection first');
    dropDown.focus(); 
  }
}
