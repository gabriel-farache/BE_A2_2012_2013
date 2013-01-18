<%-- 
    Document   : SearchMenu
    Created on : 13 janv. 2013, 21:44:24
    Author     : Doudi
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="box_left" style="width: 100%;">
    </br></br>
    <div class="text">
            <ul class="index_ul">
                    <form action=="QuickSearch.jsp" method="post">
            <p>
                     <label for="recherche">Recherche</label>
                     <input type="text" name="search" id="search" /> 
                     </br></br>
                     <input type="submit" class="submit_but" value="OK" />
                     <input type="submit" class="submit_but" value="Avancée" formaction="<c:url value="advancedSearch"/>"/>
            </p>
                    </form>
            </ul>
    </div>
</div>