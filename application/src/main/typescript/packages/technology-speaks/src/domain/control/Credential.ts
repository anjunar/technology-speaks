import {AbstractEntity, ActiveObject, Basic, Entity} from "react-ui-simplicity";
import Role from "./Role";

@Entity("Credential")
export default class Credential extends AbstractEntity {

    $type = "Credential"

    @Basic()
    displayName : string

    @Basic()
    roles : Role[]

}