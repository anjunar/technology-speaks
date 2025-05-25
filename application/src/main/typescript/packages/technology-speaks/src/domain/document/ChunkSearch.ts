import {AbstractSearch, Basic, Entity} from "react-ui-simplicity";
import Document from "./Document"

@Entity("chunkTableSearch")
export default class ChunkSearch extends AbstractSearch {

    $type = "chunkTableSearch"

    @Basic()
    document : Document

}