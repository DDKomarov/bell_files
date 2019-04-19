<table class="table table-striped">
    <thead class="thead-light">
        <tr>
            <th>File Name</th>
            <th></th>
        </tr>
    </thead>
    <tbody>
        <#list files as file>
            <tr>
                <td><a href="/download/${file.id}"><b>${file.originalName}</b></a></td>
                <td><a class="btn btn-primary" href="/delete/${file.id}">Delete</a></td>
            </tr>
        </#list>
    </tbody>
</table>