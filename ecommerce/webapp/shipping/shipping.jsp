<%@ taglib uri="ofbizTags" prefix="ofbiz" %> 
<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>
<% pageContext.setAttribute("PageName", "shippingAddress"); %>

<div align="left"><br>
  <br>
  <table width="600" border="0" cellspacing="0" cellpadding="0" align="center">
    <tr>
      <td><font size="3" face="Verdana, Arial, Helvetica, sans-serif"><b>Shipping 
        Address:</b></font></td>
    </tr>
  </table>
  <br>
</div>
<form name="addressform" method="post" action="<ofbiz:url>/setShippingAddress</ofbiz:url>">
  <table width="600" border="0" cellspacing="5" cellpadding="5" align="center">
    <tr> 
      <td><font face="Verdana, Arial, Helvetica, sans-serif" size="2">First Name</font></td>
      <td><font face="Verdana, Arial, Helvetica, sans-serif" size="2">Last Name</font></td>
    </tr>
    <tr> 
      <td> 
        <input type="text" name="firstname" size="38" maxlength="40">
      </td>
      <td> 
        <input type="text" name="lastname" size="38" maxlength="40">
      </td>
    </tr>
    <tr> 
      <td colspan="2"><font face="Verdana, Arial, Helvetica, sans-serif" size="2">Street 
        Address</font></td>
    </tr>
    <tr> 
      <td colspan="2"> 
        <input type="text" name="street1" size="80" maxlength="50">
      </td>
    </tr>
    <tr> 
      <td colspan="2"> 
        <input type="text" name="street2" size="80" maxlength="50">
      </td>
    </tr>
    <tr> 
      <td><font face="Verdana, Arial, Helvetica, sans-serif" size="2">City</font></td>
      <td><font face="Verdana, Arial, Helvetica, sans-serif" size="2">State/Province</font></td>
    </tr>
    <tr> 
      <td height="18"> 
        <input type="text" name="city" size="38" maxlength="40">
      </td>
      <td height="18"> 
        <input type="text" name="state" size="38" maxlength="20">
      </td>
    </tr>
    <tr> 
      <td><font face="Verdana, Arial, Helvetica, sans-serif" size="2">Postal/Zip 
        Code</font></td>
      <td><font face="Verdana, Arial, Helvetica, sans-serif" size="2">Country</font></td>
    </tr>
    <tr> 
      <td> 
        <input type="text" name="zipcode" size="20" maxlength="20">
      </td>
      <td> 
        <select name="country">
          <OPTION VALUE="AF">Afghanistan</OPTION>
          <OPTION VALUE="AL">Albania</OPTION>
          <OPTION VALUE="DZ">Algeria</OPTION>
          <OPTION VALUE="AD">Andorra</OPTION>
          <OPTION VALUE="AO">Angola</OPTION>
          <OPTION VALUE="AI">Anguilla</OPTION>
          <OPTION VALUE="AG">Antigua</OPTION>
          <OPTION VALUE="AN1">Antilles, Netherland</OPTION>
          <OPTION VALUE="AR">Argentina</OPTION>
          <OPTION VALUE="AM">Armenia</OPTION>
          <OPTION VALUE="AW">Aruba</OPTION>
          <OPTION VALUE="GB2">Ascension</OPTION>
          <OPTION VALUE="AU">Australia</OPTION>
          <OPTION VALUE="AT">Austria</OPTION>
          <OPTION VALUE="AZ">Azerbaijan</OPTION>
          <OPTION VALUE="PT1">Azores</OPTION>
          <OPTION VALUE="BS">Bahamas</OPTION>
          <OPTION VALUE="BH">Bahrain</OPTION>
          <OPTION VALUE="BD">Bangladesh</OPTION>
          <OPTION VALUE="BB">Barbados</OPTION>
          <OPTION VALUE="AG1">Barbuda</OPTION>
          <OPTION VALUE="BY">Belarus</OPTION>
          <OPTION VALUE="BE">Belgium</OPTION>
          <OPTION VALUE="BZ">Belize</OPTION>
          <OPTION VALUE="BJ">Benin</OPTION>
          <OPTION VALUE="BM">Bermuda</OPTION>
          <OPTION VALUE="BT">Bhutan</OPTION>
          <OPTION VALUE="BO">Bolivia</OPTION>
          <OPTION VALUE="XB">Bonaire</OPTION>
          <OPTION VALUE="BA">Bosnia-Herzegovina</OPTION>
          <OPTION VALUE="BW">Botswana</OPTION>
          <OPTION VALUE="BR">Brazil</OPTION>
          <OPTION VALUE="IO">British Indian Ocean Terr</OPTION>
          <OPTION VALUE="VG">British Virgin Islands</OPTION>
          <OPTION VALUE="BN">Brunei</OPTION>
          <OPTION VALUE="BG">Bulgaria</OPTION>
          <OPTION VALUE="BF">Burkina Faso</OPTION>
          <OPTION VALUE="MM">Burma (Myanmar)</OPTION>
          <OPTION VALUE="BI">Burundi</OPTION>
          <OPTION VALUE="KH">Cambodia (Kampuchea)</OPTION>
          <OPTION VALUE="CM">Cameroon</OPTION>
          <OPTION VALUE="CA">Canada</OPTION>
          <OPTION VALUE="IC">Canary Islands</OPTION>
          <OPTION VALUE="CV">Cape Verde</OPTION>
          <OPTION VALUE="KY">Cayman Islands</OPTION>
          <OPTION VALUE="CF">Central African Republic</OPTION>
          <OPTION VALUE="TD">Chad</OPTION>
          <OPTION VALUE="GB3">Channel Islands</OPTION>
          <OPTION VALUE="CL">Chile</OPTION>
          <OPTION VALUE="CN">China</OPTION>
          <OPTION VALUE="CX">Christmas Island</OPTION>
          <OPTION VALUE="CC">Cocos (Keeling) Island</OPTION>
          <OPTION VALUE="CO">Colombia</OPTION>
          <OPTION VALUE="KM">Comoros</OPTION>
          <OPTION VALUE="CG">Congo</OPTION>
          <OPTION VALUE="CK">Cook Islands</OPTION>
          <OPTION VALUE="FR1">Corsica</OPTION>
          <OPTION VALUE="CR">Costa Rica</OPTION>
          <OPTION VALUE="HR">Croatia</OPTION>
          <OPTION VALUE="CU">Cuba</OPTION>
          <OPTION VALUE="XC">Curacao</OPTION>
          <OPTION VALUE="CY">Cyprus</OPTION>
          <OPTION VALUE="CS">Czech Republic</OPTION>
          <OPTION VALUE="DK">Denmark</OPTION>
          <OPTION VALUE="DJ">Djibouti</OPTION>
          <OPTION VALUE="DM">Dominica</OPTION>
          <OPTION VALUE="DO">Dominican Republic</OPTION>
          <OPTION VALUE="ID1">East Timor</OPTION>
          <OPTION VALUE="EC">Ecuador</OPTION>
          <OPTION VALUE="EG">Egypt</OPTION>
          <OPTION VALUE="SV">El Salvador</OPTION>
          <OPTION VALUE="GB">England</OPTION>
          <OPTION VALUE="GQ">Equatorial Guinea</OPTION>
          <OPTION VALUE="ER">Eritrea</OPTION>
          <OPTION VALUE="EE">Estonia</OPTION>
          <OPTION VALUE="ET">Ethiopia</OPTION>
          <OPTION VALUE="FK">Falkland Islands</OPTION>
          <OPTION VALUE="FO">Faroe Islands</OPTION>
          <OPTION VALUE="FJ">Fiji</OPTION>
          <OPTION VALUE="FI">Finland</OPTION>
          <OPTION VALUE="FR">France</OPTION>
          <OPTION VALUE="GF">French Guiana</OPTION>
          <OPTION VALUE="PF">French Polynesia</OPTION>
          <OPTION VALUE="TF">French Southern/Antarctic</OPTION>
          <OPTION VALUE="GA">Gabon</OPTION>
          <OPTION VALUE="GM">Gambia</OPTION>
          <OPTION VALUE="GZ">Gaza Strip</OPTION>
          <OPTION VALUE="GE">Georgia, Republic of</OPTION>
          <OPTION VALUE="DE">Germany</OPTION>
          <OPTION VALUE="GH">Ghana</OPTION>
          <OPTION VALUE="GI">Gibraltar</OPTION>
          <OPTION VALUE="GB4">Great Britain</OPTION>
          <OPTION VALUE="GR">Greece</OPTION>
          <OPTION VALUE="GL">Greenland</OPTION>
          <OPTION VALUE="GD">Grenada</OPTION>
          <OPTION VALUE="GP">Guadeloupe</OPTION>
          <OPTION VALUE="GT">Guatemala</OPTION>
          <OPTION VALUE="GG">Guernsey</OPTION>
          <OPTION VALUE="GN">Guinea</OPTION>
          <OPTION VALUE="GW">Guinea-Bissau</OPTION>
          <OPTION VALUE="GY">Guyana, British</OPTION>
          <OPTION VALUE="HT">Haiti</OPTION>
          <OPTION VALUE="HM">Heard and Mcdonald Island</OPTION>
          <OPTION VALUE="NL1">Holland</OPTION>
          <OPTION VALUE="HN">Honduras</OPTION>
          <OPTION VALUE="HK">Hong Kong</OPTION>
          <OPTION VALUE="HU">Hungary</OPTION>
          <OPTION VALUE="IS">Iceland</OPTION>
          <OPTION VALUE="IN">India</OPTION>
          <OPTION VALUE="ID">Indonesia</OPTION>
          <OPTION VALUE="IR">Iran</OPTION>
          <OPTION VALUE="IQ">Iraq</OPTION>
          <OPTION VALUE="NT">Iraq-Saudi Neutral Zone</OPTION>
          <OPTION VALUE="IE">Ireland, Republic of</OPTION>
          <OPTION VALUE="IL">Israel</OPTION>
          <OPTION VALUE="IT">Italy</OPTION>
          <OPTION VALUE="CI">Ivory Coast</OPTION>
          <OPTION VALUE="JM">Jamaica</OPTION>
          <OPTION VALUE="JP">Japan</OPTION>
          <OPTION VALUE="JE">Jersey</OPTION>
          <OPTION VALUE="JO">Jordan</OPTION>
          <OPTION VALUE="KH1">Kampuchea</OPTION>
          <OPTION VALUE="KZ">Kazakhstan</OPTION>
          <OPTION VALUE="KE">Kenya</OPTION>
          <OPTION VALUE="KI">Kiribati</OPTION>
          <OPTION VALUE="KP">Korea, North</OPTION>
          <OPTION VALUE="KR">Korea, South</OPTION>
          <OPTION VALUE="GU2">Kosrae</OPTION>
          <OPTION VALUE="KW">Kuwait</OPTION>
          <OPTION VALUE="KG">Kyrgyzstan</OPTION>
          <OPTION VALUE="LA">Laos</OPTION>
          <OPTION VALUE="LV">Latvia</OPTION>
          <OPTION VALUE="LB">Lebanon</OPTION>
          <OPTION VALUE="LS">Lesotho</OPTION>
          <OPTION VALUE="LR">Liberia</OPTION>
          <OPTION VALUE="LY">Libya</OPTION>
          <OPTION VALUE="LI">Liechtenstein</OPTION>
          <OPTION VALUE="LT">Lithuania</OPTION>
          <OPTION VALUE="LU">Luxembourg</OPTION>
          <OPTION VALUE="MO">Macau</OPTION>
          <OPTION VALUE="MK">Macedonia, Republic of</OPTION>
          <OPTION VALUE="MG">Madagascar</OPTION>
          <OPTION VALUE="PT2">Madeira Islands</OPTION>
          <OPTION VALUE="MW">Malawi</OPTION>
          <OPTION VALUE="MY">Malaysia</OPTION>
          <OPTION VALUE="MV">Maldives</OPTION>
          <OPTION VALUE="ML">Mali</OPTION>
          <OPTION VALUE="MT">Malta</OPTION>
          <OPTION VALUE="MQ">Martinique</OPTION>
          <OPTION VALUE="MR">Mauritania</OPTION>
          <OPTION VALUE="MU">Mauritius</OPTION>
          <OPTION VALUE="MX">Mexico</OPTION>
          <OPTION VALUE="MD">Moldova</OPTION>
          <OPTION VALUE="MC">Monaco</OPTION>
          <OPTION VALUE="MN">Mongolia</OPTION>
          <OPTION VALUE="ZZ1">Montenegro</OPTION>
          <OPTION VALUE="MS">Montserrat</OPTION>
          <OPTION VALUE="MA">Morocco</OPTION>
          <OPTION VALUE="MZ">Mozambique</OPTION>
          <OPTION VALUE="MM1">Myanmar</OPTION>
          <OPTION VALUE="NA">Namibia</OPTION>
          <OPTION VALUE="NR">Nauru</OPTION>
          <OPTION VALUE="NP">Nepal</OPTION>
          <OPTION VALUE="AN">Netherland Antilles</OPTION>
          <OPTION VALUE="NL">Netherlands</OPTION>
          <OPTION VALUE="XN">Nevis</OPTION>
          <OPTION VALUE="NC">New Caledonia</OPTION>
          <OPTION VALUE="NZ">New Zealand</OPTION>
          <OPTION VALUE="NI">Nicaragua</OPTION>
          <OPTION VALUE="NE">Niger</OPTION>
          <OPTION VALUE="NG">Nigeria</OPTION>
          <OPTION VALUE="NU">Niue</OPTION>
          <OPTION VALUE="AU1">Norfolk Islands</OPTION>
          <OPTION VALUE="GB5">Northern Ireland</OPTION>
          <OPTION VALUE="NO">Norway</OPTION>
          <OPTION VALUE="OM">Oman</OPTION>
          <OPTION VALUE="PK">Pakistan</OPTION>
          <OPTION VALUE="PA">Panama</OPTION>
          <OPTION VALUE="PG">Papua New Guinea</OPTION>
          <OPTION VALUE="PY">Paraguay</OPTION>
          <OPTION VALUE="PE">Peru</OPTION>
          <OPTION VALUE="PH">Philippines</OPTION>
          <OPTION VALUE="GB6">Pitcairn Islands</OPTION>
          <OPTION VALUE="GU5">Pohnpei</OPTION>
          <OPTION VALUE="PL">Poland</OPTION>
          <OPTION VALUE="PT">Portugal</OPTION>
          <OPTION VALUE="PR">Puerto Rico</OPTION>
          <OPTION VALUE="QA">Qatar</OPTION>
          <OPTION VALUE="RE">Reunion</OPTION>
          <OPTION VALUE="RO">Romania</OPTION>
          <OPTION VALUE="GU6">Rota</OPTION>
          <OPTION VALUE="RU">Russia</OPTION>
          <OPTION VALUE="RW">Rwanda</OPTION>
          <OPTION VALUE="XM1">Saba</OPTION>
          <OPTION VALUE="MP1">Saipan</OPTION>
          <OPTION VALUE="SM">San Marino (Italy)</OPTION>
          <OPTION VALUE="ST">Sao Tome & Principe</OPTION>
          <OPTION VALUE="SA">Saudi Arabia</OPTION>
          <OPTION VALUE="GB8">Scotland</OPTION>
          <OPTION VALUE="SN">Senegal</OPTION>
          <OPTION VALUE="ZZ2">Serbia</OPTION>
          <OPTION VALUE="SC">Seychelles</OPTION>
          <OPTION VALUE="SL">Sierra Leone</OPTION>
          <OPTION VALUE="SG">Singapore</OPTION>
          <OPTION VALUE="SK">Slovakia (Slovak Republic)</OPTION>
          <OPTION VALUE="SI">Slovenia</OPTION>
          <OPTION VALUE="SB">Solomon Islands</OPTION>
          <OPTION VALUE="SO">Somalia</OPTION>
          <OPTION VALUE="ZA">South Africa</OPTION>
          <OPTION VALUE="ES">Spain</OPTION>
          <OPTION VALUE="LK">Sri Lanka</OPTION>
          <OPTION VALUE="XY">St. Barthelemy</OPTION>
          <OPTION VALUE="AI1">St. Christopher</OPTION>
          <OPTION VALUE="XE">St. Eustatius</OPTION>
          <OPTION VALUE="GB7">St. Helena</OPTION>
          <OPTION VALUE="KN">St. Kitts</OPTION>
          <OPTION VALUE="LC">St. Lucia</OPTION>
          <OPTION VALUE="XM">St. Maarten</OPTION>
          <OPTION VALUE="VI4">St. Martin</OPTION>
          <OPTION VALUE="PM">St. Pierre & Miquelon</OPTION>
          <OPTION VALUE="VC">St. Vincent</OPTION>
          <OPTION VALUE="SD">Sudan</OPTION>
          <OPTION VALUE="SR">Suriname</OPTION>
          <OPTION VALUE="SJ">Svalbard and Jan Mayen Is</OPTION>
          <OPTION VALUE="SZ">Swaziland</OPTION>
          <OPTION VALUE="SE">Sweden</OPTION>
          <OPTION VALUE="CH">Switzerland</OPTION>
          <OPTION VALUE="SY">Syria</OPTION>
          <OPTION VALUE="PF1">Tahiti</OPTION>
          <OPTION VALUE="TW">Taiwan</OPTION>
          <OPTION VALUE="TJ">Tajikistan</OPTION>
          <OPTION VALUE="TZ">Tanzania</OPTION>
          <OPTION VALUE="TH">Thailand</OPTION>
          <OPTION VALUE="GU7">Tinian</OPTION>
          <OPTION VALUE="TG">Togo</OPTION>
          <OPTION VALUE="TK">Tokelau</OPTION>
          <OPTION VALUE="TO">Tonga</OPTION>
          <OPTION VALUE="VG1">Tortola</OPTION>
          <OPTION VALUE="TT">Trinidad and Tobago</OPTION>
          <OPTION VALUE="GB9">Tristan Da Cunha</OPTION>
          <OPTION VALUE="GU8">Truk</OPTION>
          <OPTION VALUE="TN">Tunisia</OPTION>
          <OPTION VALUE="TR">Turkey</OPTION>
          <OPTION VALUE="TM">Turkmenistan</OPTION>
          <OPTION VALUE="TC">Turks & Caicos Islands</OPTION>
          <OPTION VALUE="TV">Tuvalu</OPTION>
          <OPTION VALUE="UG">Uganda</OPTION>
          <OPTION VALUE="UA">Ukraine</OPTION>
          <OPTION VALUE="WS1">Union Island</OPTION>
          <OPTION VALUE="AE">United Arab Emirates</OPTION>
          <OPTION VALUE="GB1">United Kingdom</OPTION>
          <OPTION VALUE="US" SELECTED>United States</OPTION>
          <OPTION VALUE="UY">Uruguay</OPTION>
          <OPTION VALUE="UM">US Minor Outlying Islands</OPTION>
          <OPTION VALUE="UZ">Uzbekistan</OPTION>
          <OPTION VALUE="VU">Vanuatu</OPTION>
          <OPTION VALUE="VA">Vatican City</OPTION>
          <OPTION VALUE="VE">Venezuela</OPTION>
          <OPTION VALUE="VN">Vietnam</OPTION>
          <OPTION VALUE="VG2">Virgin Islands (British)</OPTION>
          <OPTION VALUE="WA">Wake Island</OPTION>
          <OPTION VALUE="GBA">Wales</OPTION>
          <OPTION VALUE="WF">Wallis & Futuna Islands</OPTION>
          <OPTION VALUE="WE">West Bank</OPTION>
          <OPTION VALUE="WS">Western Samoa</OPTION>
          <OPTION VALUE="EH">Western Saraha</OPTION>
          <OPTION VALUE="GU9">Yap</OPTION>
          <OPTION VALUE="YE">Yemen</OPTION>
          <OPTION VALUE="YU">Yugoslavia</OPTION>
          <OPTION VALUE="ZR">Zaire</OPTION>
          <OPTION VALUE="ZM">Zambia</OPTION>
          <OPTION VALUE="ZW">Zimbabwe</OPTION>
        </select>
      </td>
    </tr>
    <tr> 
      <td><font face="Verdana, Arial, Helvetica, sans-serif" size="2">Phone Number</font></td>
      <td><font face="Verdana, Arial, Helvetica, sans-serif" size="2">Email Address</font></td>
    </tr>
    <tr> 
      <td> 
        <input type="text" name="phone1" size="30" maxlength="38">
      </td>
      <td> 
        <input type="text" name="email1" size="38" maxlength="128">
      </td>
    </tr>
    <tr> 
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
    <tr> 
      <td colspan="2" height="30"> 
        <input CHECKED type="checkbox" name="same_billing" value="Y">
        <font face="Verdana, Arial, Helvetica, sans-serif" size="1">The Billing 
        address is the same as this shipping address</font> </td>
    </tr>
    <tr> 
      <td colspan="2">&nbsp;</td>
    </tr>	
    <tr> 
      <td colspan="2"> 
        <div align="center"> 
          <input type="hidden" name="address_type" value="S">
          <input type="submit" value="CONTINUE">
        </div>
      </td>
    </tr>
  </table>
</form>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>

