/// A simple abstraction of a writer which would be used for file or CLI writing.
///
/// # Parameters
///
/// * `C` - A context structure for the writing.
pub trait Writer<C> {
    /// Writes the data from the given context.
    ///
    /// # Arguments
    ///
    /// * `context` - The context of the writing.
    ///
    /// # Returns
    ///
    /// * `Result<(), Error>` - The result of the writing.
    fn write(&self, context: &C) -> Result<(), &str>;
}
