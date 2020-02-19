package app.serialcom

/**
 * This interface should be implemented by every class that wants to keep
 * the list of available serial communication ports up to date.
 * The method should update the list by taking the up-to-date list
 * of ports using [SerialCommunication.getAllPorts] method.
 */
interface OnAvailablePortsChangeListener {
    /**
     * Invoked by the [SerialCommunication] class whenever the list of
     * available ports has been modified
     */
    fun onAvailablePortsChange()
}