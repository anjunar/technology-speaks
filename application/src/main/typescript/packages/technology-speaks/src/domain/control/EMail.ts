import {Basic, Entity, AbstractEntity} from "react-ui-simplicity";

@Entity("eMail")
export default class EMail extends AbstractEntity {

    $type = "eMail"

    @Basic()
    value : string

}