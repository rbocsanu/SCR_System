import operatorUser from '../assets/operatoruser.png'
import '../index.css'

function ServerUser(props) {
    return (
        <div className={'serverUser'}>
            <div className='query' data-querytype={props.queryType}>{props.queryText}</div>
            <img src={operatorUser} alt={''}/>
        </div>
    )
}

export default ServerUser