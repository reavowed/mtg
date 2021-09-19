import {useState} from "preact/hooks";
import {Button, Modal} from "react-bootstrap";
import BannerText from "../../layout/BannerText";
import HorizontalCenter from "../../layout/HorizontalCenter";

export default function PopupChoice({text, children}) {
    const [showModal, setShowModal] = useState(true);
    return <div>
        <BannerText as="p">{text}</BannerText>
        <HorizontalCenter>
            {!showModal && <Button onClick={() => setShowModal(true)}>Show</Button>}
        </HorizontalCenter>
        <Modal show={showModal} onHide={() => setShowModal(false)}>
            <Modal.Body>
                {children}
            </Modal.Body>
        </Modal>
    </div>;
}
