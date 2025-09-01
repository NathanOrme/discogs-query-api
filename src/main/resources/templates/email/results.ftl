<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1"/>
  <title>Discogs Query Results</title>
</head>
<body style="margin:0;padding:24px;background:#f9fafb;color:#1f2937;font-family:Arial,Helvetica,sans-serif;">
  <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0">
    <tr>
      <td align="center">
        <table role="presentation" width="720" cellpadding="0" cellspacing="0" border="0" style="max-width:720px;width:100%;background:#ffffff;border:1px solid #e5e7eb;border-radius:10px;box-shadow:0 1px 2px rgba(0,0,0,0.06);overflow:hidden;">
          <tr>
            <td style="background:#111827;color:#ffffff;padding:16px 20px;"><h1 style="margin:0;font-size:20px;">Discogs Query Results</h1></td>
          </tr>
          <tr>
            <td style="padding:20px;">

              <#list results as r>
                <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="margin:0 0 16px 0;">
                  <tr>
                    <td style="background:#f3f4f6;border-left:4px solid #3b82f6;border-radius:6px;padding:12px;">
                      <div style="font-weight:700;color:#111827;margin:0 0 8px 0;">Query: ${r.queryLabel?html}</div>

                      <#if r.cheapest??>
                        <div style="color:#065f46;background:#ecfdf5;border:1px solid #a7f3d0;border-radius:6px;padding:8px 10px;margin:8px 0;display:inline-block;">
                          <#if r.cheapest.link?has_content>
                            <a href="${r.cheapest.link?html}" style="color:#065f46;text-decoration:none;font-weight:600;">${r.cheapest.title?html}</a>
                          <#else>
                            ${r.cheapest.title?html}
                          </#if>
                          — ${r.cheapest.price?html}
                          <#if r.cheapest.meta?has_content>
                            <div style="font-size:12px;color:#065f46;opacity:0.9;margin-top:4px;">${r.cheapest.meta?html}</div>
                          </#if>
                        </div>
                      </#if>

                      <#list r.groups as g>
                        <div style="margin-top:8px;">
                          <div style="font-weight:600;color:#374151;margin-bottom:4px;">${g.name?html}:</div>
                          <ul style="margin:6px 0 0 18px;padding:0;">
                            <#list g.items as it>
                              <li style="margin:2px 0;">
                                <#if it.link?has_content>
                                  <a href="${it.link?html}" style="color:#1d4ed8;text-decoration:none;">${it.title?html}</a>
                                <#else>
                                  ${it.title?html}
                                </#if>
                                <#if it.meta?has_content>
                                  <span style="color:#6b7280;font-size:12px;"> ${it.meta?html}</span>
                                </#if>
                              </li>
                            </#list>
                          </ul>
                        </div>
                      </#list>

                    </td>
                  </tr>
                </table>
              </#list>

            </td>
          </tr>
          <tr>
            <td style="font-size:12px;color:#6b7280;padding:16px 20px;border-top:1px solid #e5e7eb;background:#f9fafb;">Sent by Discogs Query</td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</body>
</html>
