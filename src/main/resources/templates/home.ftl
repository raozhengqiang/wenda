<html>
<body>
<pre>
    ${value1}
    <#list colors as color>
    	This is Color ${color_index}: ${color}
    </#list>
    
	<#list map?keys as key>
		Number:${key}, Value:${map[key]}
	</#list>
	





</pre>
</body>
</html>