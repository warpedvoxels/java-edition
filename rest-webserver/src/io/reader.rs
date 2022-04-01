/// A simple abstraction of a reader which would be used for file or CLI reading.
///
/// # Parameters
///
/// * `T` - The type of the data that is being read.
/// * `C` - A context structure for the reading.
pub trait Reader<T, C> {
    /// Reads the data from the given context.
    ///
    /// # Arguments
    ///
    /// * `context` - The context of the reading.
    ///
    /// # Returns
    ///
    /// * `Result<T, Error>` - The data read from the reader.
    fn read(context: &C) -> Result<T, &str>;
}

