package com.example.data

enum class BankProvider {
  TELEBIRR,
  CBE,
  ABYSSINIA,
}

data class BankAccount(
  val id: String,
  val displayName: String,
  val methodOption: String,
  val accountNumber: String,
  val ownerName: String,
  val provider: BankProvider,
)

data class PaymentReceipt(
  val id: String,
  val method: String,
  val transactionRef: String,
  val submittedAt: String,
)

enum class PortalScreenMode {
  PAYMENT_FORM,
  STATUS_OVERVIEW,
  CONFIRMED_RECEIPT,
}

object PaymentAccountsCatalog {
  val accounts: List<BankAccount> =
    listOf(
      BankAccount(
        id = "telebirr_1",
        displayName = "ቴሌብር (Telebirr)",
        methodOption = "ቴሌብር (0942320888)",
        accountNumber = "0942320888",
        ownerName = "አዲሴ አራጋው",
        provider = BankProvider.TELEBIRR,
      ),
      BankAccount(
        id = "telebirr_2",
        displayName = "ቴሌብር (Telebirr 2)",
        methodOption = "ቴሌብር (0925792262)",
        accountNumber = "0925792262",
        ownerName = "አዲሴ አራጋው",
        provider = BankProvider.TELEBIRR,
      ),
      BankAccount(
        id = "cbe",
        displayName = "የኢትዮጵያ ንግድ ባንክ (CBE)",
        methodOption = "ንግድ ባንክ (CBE)",
        accountNumber = "1000282540634",
        ownerName = "አዲሴ አራጋው",
        provider = BankProvider.CBE,
      ),
      BankAccount(
        id = "boa",
        displayName = "አቢሲኒያ ባንክ (Abyssinia)",
        methodOption = "አቢሲኒያ ባንክ",
        accountNumber = "66833787",
        ownerName = "አዲሴ አራጋው",
        provider = BankProvider.ABYSSINIA,
      ),
    )

  val paymentMethodOptions: List<String> = accounts.map { it.methodOption }
}
