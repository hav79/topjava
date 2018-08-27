var ajaxUrl = "ajax/profile/meals/";
var datatableApi;

// $(document).ready(function () {
$(function () {
    datatableApi = $("#datatable").DataTable({
        "paging": false,
        "info": true,
        "columns": [
            {
                "data": "dateTime"
            },
            {
                "data": "description"
            },
            {
                "data": "calories"
            },
            {
                "defaultContent": "Edit",
                "orderable": false
            },
            {
                "defaultContent": "Delete",
                "orderable": false
            }
        ],
        "order": [
            [
                0,
                "asc"
            ]
        ]
    });
    makeEditable();
});

function updateTable() {
    var form = $("#filterForm");
    $.post(ajaxUrl + "filter",
        form.serialize(),
        function (data) {
            alert(data);
            datatableApi.clear().rows.add(data).draw();
            successNoty("Updated");
        },
        'json'
    );
}

function resetFilter() {
    $("#filterForm").find(":input").val("");
    updateTable();
}