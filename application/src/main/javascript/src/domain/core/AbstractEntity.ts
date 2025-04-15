import {v4} from "uuid";
import {ActiveObject, Basic, MappedSuperclass} from "react-ui-simplicity";

@MappedSuperclass("AbstractEntity")
export default class AbstractEntity extends ActiveObject {

    @Basic()
    id: string = v4()

}