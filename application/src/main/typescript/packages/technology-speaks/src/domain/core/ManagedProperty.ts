import {AbstractEntity, Basic, Entity} from "react-ui-simplicity";
import Group from "../control/Group";

@Entity("managedProperty")
export default class ManagedProperty extends AbstractEntity {

    $type = "managedProperty"

    @Basic()
    visibleForAll : boolean

    @Basic()
    groups : Group[]

}