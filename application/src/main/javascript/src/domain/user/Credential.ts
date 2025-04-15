import {ActiveObject, Basic, Entity} from "react-ui-simplicity";

@Entity("Credential")
export default class Credential extends ActiveObject {

    $type = "Credential"

    @Basic()
    displayName : string

}