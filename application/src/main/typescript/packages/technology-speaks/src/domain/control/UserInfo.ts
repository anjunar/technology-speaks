import {LocalDate} from "@js-joda/core";
import {AbstractEntity, Basic, Entity, Media} from "react-ui-simplicity";

@Entity("userInfo")
export default class UserInfo extends AbstractEntity {

    $type = "userInfo"

    @Basic()
    firstName : string = ""

    @Basic()
    lastName : string = ""

    @Basic({
        default : null
    })
    image : Media

    @Basic({
        default : LocalDate.now
    })
    birthDate : LocalDate

}