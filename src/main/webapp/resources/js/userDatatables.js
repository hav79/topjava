var ajaxUrl = "ajax/admin/users/";
var datatableApi;

// $(document).ready(function () {
$(function () {
    datatableApi = $("#datatable").DataTable({
        "paging": false,
        "info": true,
        "columns": [
            {
                "data": "name"
            },
            {
                "data": "email"
            },
            {
                "data": "roles"
            },
            {
                "data": "enabled"
            },
            {
                "data": "registered"
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
    $(".enabled").change(function () {
        setEnabled($(this).parents("tr").attr("id"), $(this).prop("checked"));
    })
});

function updateTable() {
    $.get(ajaxUrl, function (data) {
        datatableApi.clear().rows.add(data).draw();
    });
}

function setEnabled(id, enabled) {
    $.post(ajaxUrl + id, {enabled: enabled}, function () {
        updateTable();
    });
}