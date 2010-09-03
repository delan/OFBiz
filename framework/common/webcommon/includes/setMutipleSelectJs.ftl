<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->
<script type="text/javascript">
  jQuery(document).ready(function() {

    jQuery("#${multipleSelectForm}_${multipleSelect}").asmSelect({
      addItemTarget: 'top',
      sortable: ${sortable}
    });

    // track changes with our own event
    jQuery("#${multipleSelect}").change(function(e, data) {
      // if it's a sort or an add, then give it a little color animation to highlight it
      if(data.type != 'drop') data.item.animate({ 'backgroundColor': '#ffffcc' }, 20, 'linear', function() {
        data.item.animate({ 'backgroundColor': '#dddddd' }, 500); 
      }); 
    }); 

  }); 
</script>

<style type="text/css">
  #${multipleSelectForm} {
    width: ${formSize}px; 
    position: relative;
  }
.asmListItem {
  width: ${asmListItemPercentOfForm}%; 
}
</style>
