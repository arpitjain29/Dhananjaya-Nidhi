package com.dhananjayanidhi.models.memberdraft

import com.dhananjayanidhi.models.CommonModel
import com.dhananjayanidhi.models.CommonNewModel
import com.google.gson.annotations.SerializedName

class MemberDraftListModel : CommonNewModel() {
    @SerializedName("data")
    var data: List<DatumMemberDraftModel>? = null
}

