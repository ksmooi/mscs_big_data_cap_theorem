<#import "template.ftl" as layout />

<@layout.noauthentication>
    <section>
        <div class="container">
            <h2>Products</h2>

            <#if products??>
                <div>
                    <table>
                        <thead>
                        <tr>
                            <th>Id</th>
                            <th>Name</th>
                            <th>Quantity</th>
                        </tr>
                        </thead>
                        <#list products as product>
                            <tbody>
                            <tr>
                                <td>
                                    ${product.id?c}
                                </td>
                                <td>
                                    ${product.name}
                                </td>
                                <td>
                                    ${product.quantity}
                                </td>
                            </tr>
                            </tbody>
                        </#list>
                    </table>
                </div>
            </#if>
        </div>
    </section>

</@layout.noauthentication>