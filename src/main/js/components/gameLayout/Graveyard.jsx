import {useContext, useState} from "preact/hooks";
import GameState from "../../contexts/GameState";
import {addClass} from "../../utils/element-utils";
import _ from "lodash";
import CardImage from "../card/CardImage";
import {Modal} from "react-bootstrap";
import CardRow from "../card/CardRow";

export default function Graveyard({player, className, ...props}) {
    const gameState = useContext(GameState);
    const graveyard = gameState.graveyards[player]
    const graveyardSize = _.isNumber(graveyard) ? graveyard : graveyard.length;
    const topCard = graveyardSize > 0 && graveyard[graveyardSize - 1];
    const [showModal, setShowModal] = useState(false);

    const zoneElement =  graveyardSize > 0 ?
        <div onClick={() => setShowModal(true)} className={addClass(className, "zoneWithCount graveyard")} {...props}>
            <CardImage card={topCard} key={topCard.objectId} />
            <span className="zoneCount">{graveyardSize}</span>
        </div> :
        <div className={addClass(className, "graveyardOutline")} {...props}/>;
    return <div className="zoneContainer">
        <div className="zoneLabel">Graveyard</div>
        {zoneElement}
        <Modal show={showModal} onHide={() => setShowModal(false)}>
            <Modal.Body>
                <CardRow cards={graveyard} />
            </Modal.Body>
        </Modal>
    </div>
}
