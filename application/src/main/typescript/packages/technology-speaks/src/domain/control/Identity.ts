import {Basic, Entity, AbstractEntity} from "react-ui-simplicity";

@Entity("identity")
export default class Identity extends AbstractEntity {

    $type = "identity"

    @Basic()
    enabled : boolean

    @Basic()
    deleted : boolean

}