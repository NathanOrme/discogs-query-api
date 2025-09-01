Discogs Query Results
======================

<#if results?size == 0>
No results to display.
<#else>
<#list results as r>
${r?index + 1}) Query: ${r.queryLabel}

<#if r.cheapest??>
  • Cheapest: ${r.cheapest.title} — ${r.cheapest.price}<#if r.cheapest.meta?has_content> (${r.cheapest.meta})</#if>
  <#if r.cheapest.link?has_content>
    → ${r.cheapest.link}
  </#if>
</#if>

<#list r.groups as g>
  ${g.name}:
  <#list g.items as it>
    - ${it.title}<#if it.meta?has_content> (${it.meta})</#if>
    <#if it.link?has_content>
      → ${it.link}
    </#if>
  </#list>
</#list>

------------------------------------------------------------

</#list>
</#if>
