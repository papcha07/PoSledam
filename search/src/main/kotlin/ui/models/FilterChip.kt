package ui.models

sealed class FilterChip(val id: String, val label: String) {
    object District : FilterChip("district", "")
    object Time : FilterChip("time", "")
    object Type : FilterChip("type", "")
    object Gender : FilterChip("gender", "")
}