import {Basic, Entity, AbstractEntity} from "react-ui-simplicity";

@Entity("role")
export default class Role extends AbstractEntity {

    $type = "role"

    @Basic()
    name : string

    @Basic()
    description : string

}