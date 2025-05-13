import {ActiveObject, Basic, Entity} from "react-ui-simplicity";

@Entity("confirmation")
export default class Confirmation extends ActiveObject {

    $type = "confirmation"

    @Basic()
    code : string

}