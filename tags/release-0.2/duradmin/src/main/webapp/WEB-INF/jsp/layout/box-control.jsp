<%@include file="/WEB-INF/jsp/include.jsp"%>
		<table class="boxcontrol">
			<tr>
				<td class="title">
					<div style="float:left; width:50%;">
						<tiles:insertAttribute name="title"/>
					</div>
					<div style="text-align:right;float:right; width:50%;">
						<tiles:insertAttribute name="titlebuttons" ignore="true" />
					</div>
				</td>
			</tr>
			<tr>
				<td>
					<div id="miniform" class="miniform" style="display:none">
						<tiles:insertAttribute name="miniform"/>
					</div>
					<div class="body">
						<tiles:insertAttribute name="body"/>
					</div>				
				</td>
			</tr>
		</table>