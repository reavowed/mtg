import _ from "lodash";
import {useContext, useState} from "preact/hooks";
import {Button, Modal} from "react-bootstrap";
import GameState from "../../../contexts/GameState";
import BannerText from "../../layout/BannerText";
import HorizontalCenter from "../../layout/HorizontalCenter";
import CardImage from "../card/CardImage";
import DecisionButton from "../DecisionButton";

export default function AssignCombatDamageChoice() {
    const gameState = useContext(GameState);
    const [showModal, setShowModal] = useState(true);

    const player = gameState.player;
    const opponent = _.find(gameState.gameData.playersInTurnOrder, p => p !== player);

    const attacker = _.find(gameState.battlefield[player], o => o.objectId === gameState.currentChoice.details.attacker);
    const blockers = _.map(gameState.currentChoice.details.blockers, ([blocker, damage]) => [_.find(gameState.battlefield[opponent], o => o.objectId === blocker), damage]);

    const [assignedDamage, setAssignedDamage] = useState(_.reduce(blockers, (result, [blocker, damageRequired]) => {
        const damage = Math.min(result.remainingDamage, damageRequired);
        return {
            ...result,
            [blocker.objectId]: damage,
            remainingDamage: result.remainingDamage - damage
        }
    }, {remainingDamage: gameState.currentChoice.details.damageToAssign}));

    const serializedChoice = _.flatMap(blockers, ([blocker]) => [blocker.objectId, assignedDamage[blocker.objectId]]).join(" ")

    return <div>
        <BannerText as="p">Assign Damage</BannerText>
        <HorizontalCenter>
            {!showModal && <Button onClick={() => setShowModal(true)}>Show</Button>}
        </HorizontalCenter>
        <Modal show={showModal} onHide={() => setShowModal(false)}>
            <Modal.Body>
                <HorizontalCenter>
                    {blockers.map(([blocker, damage], index) => <div className={index > 0 && "ml-2"}>
                        <CardImage card={blocker}/>
                        <HorizontalCenter>
                            <input className="form-control text-center mt-2" style={{width: "50px"}} value={assignedDamage[blocker.objectId]} onChange={e => setAssignedDamage({...assignedDamage, [blocker.objectId]: e.target.value})} />
                        </HorizontalCenter>
                    </div>)}
                </HorizontalCenter>
                <HorizontalCenter>
                    <CardImage card={attacker}/>
                </HorizontalCenter>
                <BannerText>Assign Damage</BannerText>
                <HorizontalCenter>
                    <DecisionButton optionToChoose={serializedChoice}>Submit</DecisionButton>
                </HorizontalCenter>
            </Modal.Body>
        </Modal>
    </div>;
}
