// Run with: firebase emulators:exec --only firestore --project demo-pulsefinance "npm test"
// (from this folder, after `npm install`). Needs JDK 21+ for the emulator.
//
// The documents below are what the app writes: the fields of ExpenseDto, IncomeDto, GoalDto and
// AccountDto, with userId set by FirestoreDataSource. If a DTO gains a field, add it here.

const { test, before, after, beforeEach } = require('node:test');
const fs = require('node:fs');
const path = require('node:path');
const {
  initializeTestEnvironment,
  assertSucceeds,
  assertFails,
} = require('@firebase/rules-unit-testing');
const { doc, setDoc, getDoc, updateDoc, deleteDoc, getDocs, collection } = require('firebase/firestore');

const KAVINDU = 'kavindu-uid';
const OTHER = 'someone-else-uid';
const NOW = 1_785_000_000_000;

let env;

before(async () => {
  env = await initializeTestEnvironment({
    projectId: 'demo-pulsefinance',
    firestore: { rules: fs.readFileSync(path.join(__dirname, '..', 'firestore.rules'), 'utf8') },
  });
});

after(async () => env.cleanup());
beforeEach(async () => env.clearFirestore());

const asKavindu = () => env.authenticatedContext(KAVINDU).firestore();
const asOther = () => env.authenticatedContext(OTHER).firestore();
const signedOut = () => env.unauthenticatedContext().firestore();

const expense = (over = {}) => ({
  userId: KAVINDU, title: 'Lunch', amountLkr: 1450, category: 'FOOD_DINING', isCommitted: false,
  paymentAccountId: 'acc_primary', paymentAccountName: 'Primary Bank Account', timestamp: NOW, notes: '', ...over,
});

const income = (over = {}) => ({
  userId: KAVINDU, source: 'FREELANCE', title: 'Milestone 2', clientOrPlatform: 'LankaRetail',
  originalAmount: 150000, currency: 'LKR', exchangeRateToLkr: 1, amountInLkr: 150000,
  status: 'PENDING_INVOICE', dateReceivedOrExpected: NOW, isRecurringMonthly: false, notes: '', ...over,
});

const goal = (over = {}) => ({
  userId: KAVINDU, title: 'MacBook Pro', targetAmountLkr: 850000, currentSavedLkr: 120000,
  targetDateMillis: NOW + 90 * 86_400_000, createdDateMillis: NOW, notes: '',
  usdReferencePrice: 2799, usdRateAtSetLkr: 304, usdRateSetAtMillis: NOW, ...over,
});

const account = (over = {}) => ({
  userId: KAVINDU, name: 'Primary Bank Account', type: 'BANK_PRIMARY', balanceLkr: 0,
  isAutoDebitSource: false, accountNumber: '', isFrozen: false, frozenUntilMillis: null, freezeBreakCount: 0, ...over,
});

const at = (db, ...segments) => doc(db, 'users', KAVINDU, ...segments);

// ---- Ownership ------------------------------------------------------------------------------

test('a signed-out visitor can read and write nothing', async () => {
  await assertFails(getDoc(doc(signedOut(), 'users', KAVINDU, 'expenses', 'e1')));
  await assertFails(setDoc(doc(signedOut(), 'users', KAVINDU, 'expenses', 'e1'), expense()));
});

test("someone else cannot read, write or delete Kavindu's records", async () => {
  await env.withSecurityRulesDisabled(async (ctx) => {
    await setDoc(doc(ctx.firestore(), 'users', KAVINDU, 'expenses', 'e1'), expense());
  });
  const db = asOther();
  await assertFails(getDoc(at(db, 'expenses', 'e1')));
  await assertFails(setDoc(at(db, 'expenses', 'e2'), expense({ userId: OTHER })));
  await assertFails(deleteDoc(at(db, 'expenses', 'e1')));
  await assertFails(getDocs(collection(db, 'users', KAVINDU, 'expenses')));
});

test('a record must carry its owner’s uid', async () => {
  await assertFails(setDoc(at(asKavindu(), 'expenses', 'e1'), expense({ userId: OTHER })));
});

// ---- Expenses -------------------------------------------------------------------------------

test('the app’s expense document is accepted, then edited and deleted (FR-EXP-8)', async () => {
  const db = asKavindu();
  await assertSucceeds(setDoc(at(db, 'expenses', 'e1'), expense()));
  await assertSucceeds(setDoc(at(db, 'expenses', 'e1'), expense({ amountLkr: 1850, title: 'Lunch with team' })));
  await assertSucceeds(deleteDoc(at(db, 'expenses', 'e1')));
});

test('expenses that make no sense are refused', async () => {
  const db = asKavindu();
  await assertFails(setDoc(at(db, 'expenses', 'a'), expense({ amountLkr: -5 })));
  await assertFails(setDoc(at(db, 'expenses', 'b'), expense({ amountLkr: '1450' })));
  await assertFails(setDoc(at(db, 'expenses', 'c'), expense({ category: 'GAMBLING' })));
  await assertFails(setDoc(at(db, 'expenses', 'd'), expense({ title: 'x'.repeat(201) })));
  await assertFails(setDoc(at(db, 'expenses', 'e'), expense({ timestamp: 'yesterday' })));
});

// ---- Income ---------------------------------------------------------------------------------

test('the app’s income documents are accepted: rupee, dollar and a crypto loss', async () => {
  const db = asKavindu();
  await assertSucceeds(setDoc(at(db, 'income_streams', 'i1'), income()));
  await assertSucceeds(setDoc(at(db, 'income_streams', 'i2'), income({
    source: 'ADSENSE', currency: 'USD', originalAmount: 412.5, exchangeRateToLkr: 301.25, amountInLkr: 124265.63, status: 'RECEIVED',
  })));
  await assertSucceeds(setDoc(at(db, 'income_streams', 'i3'), income({
    source: 'CRYPTO', currency: 'USDT', originalAmount: -50, exchangeRateToLkr: 305, amountInLkr: -15250, status: 'RECEIVED',
  })));
});

test('marking an invoice received is an update the rules allow (FR-INV-3)', async () => {
  const db = asKavindu();
  await assertSucceeds(setDoc(at(db, 'income_streams', 'i1'), income()));
  await assertSucceeds(updateDoc(at(db, 'income_streams', 'i1'), { status: 'RECEIVED', dateReceivedOrExpected: NOW + 1000 }));
});

test('income with a bad source, currency, status or rate is refused', async () => {
  const db = asKavindu();
  await assertFails(setDoc(at(db, 'income_streams', 'a'), income({ source: 'LOTTERY' })));
  await assertFails(setDoc(at(db, 'income_streams', 'b'), income({ currency: 'BTC' })));
  await assertFails(setDoc(at(db, 'income_streams', 'c'), income({ status: 'PAID' })));
  await assertFails(setDoc(at(db, 'income_streams', 'd'), income({ exchangeRateToLkr: 0 })));
  await assertFails(setDoc(at(db, 'income_streams', 'e'), income({ amountInLkr: 'lots' })));
});

// ---- Goals ----------------------------------------------------------------------------------

test('the app’s goal document is accepted, with or without a USD target (FR-GOAL-6)', async () => {
  const db = asKavindu();
  await assertSucceeds(setDoc(at(db, 'goals', 'g1'), goal()));
  await assertSucceeds(setDoc(at(db, 'goals', 'g2'), goal({ usdReferencePrice: null, usdRateAtSetLkr: null, usdRateSetAtMillis: null })));
});

test('adding to a goal is an update the rules allow, but not below zero', async () => {
  const db = asKavindu();
  await assertSucceeds(setDoc(at(db, 'goals', 'g1'), goal()));
  await assertSucceeds(updateDoc(at(db, 'goals', 'g1'), { currentSavedLkr: 150000 }));
  await assertFails(updateDoc(at(db, 'goals', 'g1'), { currentSavedLkr: -1 }));
});

// ---- Accounts -------------------------------------------------------------------------------

test('the app’s account document is accepted, including a frozen one with a Freeze Break count', async () => {
  const db = asKavindu();
  await assertSucceeds(setDoc(at(db, 'accounts', 'a1'), account()));
  await assertSucceeds(setDoc(at(db, 'accounts', 'a2'), account({
    name: 'Secondary Account (Auto-Debits)', type: 'BANK_SECONDARY', isAutoDebitSource: true,
    isFrozen: true, frozenUntilMillis: NOW + 86_400_000, freezeBreakCount: 2, accountNumber: '8012345678',
  })));
});

test('an account balance can be set, even negative for a card he owes on (FR-ACC-9)', async () => {
  const db = asKavindu();
  await assertSucceeds(setDoc(at(db, 'accounts', 'a1'), account({ balanceLkr: 83000 })));
  await assertSucceeds(setDoc(at(db, 'accounts', 'a1'), account({ balanceLkr: -12500 })));
  await assertSucceeds(deleteDoc(at(db, 'accounts', 'a1')));
});

test('accounts with a bad type, balance or number are refused', async () => {
  const db = asKavindu();
  await assertFails(setDoc(at(db, 'accounts', 'a'), account({ type: 'OFFSHORE' })));
  await assertFails(setDoc(at(db, 'accounts', 'b'), account({ balanceLkr: 'rich' })));
  await assertFails(setDoc(at(db, 'accounts', 'c'), account({ accountNumber: '1'.repeat(35) })));
  await assertFails(setDoc(at(db, 'accounts', 'd'), account({ freezeBreakCount: -1 })));
});

// ---- The user document (reviews, reserves, onboarding) --------------------------------------

test('the owner can merge onboarding, runway, reviews and reserves into the user document', async () => {
  const db = asKavindu();
  await assertSucceeds(setDoc(at(db), { onboarding: { runwayMonths: 6, runwaySavedLkr: 100000 } }, { merge: true }));
  await assertSucceeds(setDoc(at(db), { subscriptionReviews: { netflix: { decision: 'CANCEL' } } }, { merge: true }));
  await assertSucceeds(setDoc(at(db), { seasonalReserves: { avurudu: { amountLkr: 20000 } } }, { merge: true }));
  await assertSucceeds(getDoc(at(db)));
});

test('nobody else can touch the user document', async () => {
  await assertFails(setDoc(at(asOther()), { onboarding: {} }, { merge: true }));
  await assertFails(getDoc(at(asOther())));
});

// ---- Account deletion (FR-AUTH-9) ------------------------------------------------------------

test('he can erase every collection and the user document', async () => {
  const db = asKavindu();
  await assertSucceeds(setDoc(at(db, 'expenses', 'e1'), expense()));
  await assertSucceeds(setDoc(at(db, 'income_streams', 'i1'), income()));
  await assertSucceeds(setDoc(at(db, 'goals', 'g1'), goal()));
  await assertSucceeds(setDoc(at(db, 'accounts', 'a1'), account()));
  await assertSucceeds(setDoc(at(db), { onboarding: {} }, { merge: true }));

  await assertSucceeds(deleteDoc(at(db, 'expenses', 'e1')));
  await assertSucceeds(deleteDoc(at(db, 'income_streams', 'i1')));
  await assertSucceeds(deleteDoc(at(db, 'goals', 'g1')));
  await assertSucceeds(deleteDoc(at(db, 'accounts', 'a1')));
  await assertSucceeds(deleteDoc(at(db)));
});

// ---- Shared config ----------------------------------------------------------------------------

test('APIT bands are readable when signed in and never writable from the app (FR-LK-2)', async () => {
  await env.withSecurityRulesDisabled(async (ctx) => {
    await setDoc(doc(ctx.firestore(), 'config', 'apitBands'), { bands: [] });
  });
  await assertSucceeds(getDoc(doc(asKavindu(), 'config', 'apitBands')));
  await assertFails(getDoc(doc(signedOut(), 'config', 'apitBands')));
  await assertFails(setDoc(doc(asKavindu(), 'config', 'apitBands'), { bands: [] }));
});

test('anything outside the known paths is closed', async () => {
  await assertFails(setDoc(doc(asKavindu(), 'public', 'x'), { a: 1 }));
  await assertFails(getDoc(doc(asKavindu(), 'users', KAVINDU, 'secrets', 's1')));
});
